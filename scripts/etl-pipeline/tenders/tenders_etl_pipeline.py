import os
import re
import logging
from datetime import datetime, timedelta
from typing import Optional, Dict, Any, List
import xml.etree.ElementTree as ET
from dotenv import load_dotenv
from supabase import create_client, Client
import requests
from bs4 import BeautifulSoup
from dateutil.parser import parse

# ---------------------------
# Configuration
# ---------------------------

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler()]
)

# Load environment variables
load_dotenv('../../.env.local')

# Constants
CONFIG = {
    'ALLOWED_CATEGORIES': {
        'IT & Digitalisierung': [
            'IT', 'Software', 'Hardware', 'Systeme', 
            'Datenbank', 'Netzwerk', 'Digital'
        ],
        'Dienstleistungen': [
            'Dienstleistungen', 'Service', 'Beratung'
        ]
    },
    'RSS_FEED': 'https://www.service.bund.de/Content/Globals/Functions/RSSFeed/RSSGenerator_Ausschreibungen.xml',
    'SOURCE_NAME': 'service.bund.de Tenders'
}

# ---------------------------
# Database Client
# ---------------------------

def get_supabase_client() -> Client:
    """Initialize and return Supabase client"""
    supabase_url = os.getenv('NEXT_PUBLIC_SUPABASE_URL')
    supabase_key = os.getenv('NEXT_PUBLIC_SUPABASE_ANON_KEY')
    
    if not supabase_url or not supabase_key:
        raise ValueError('Missing Supabase environment variables')
    
    return create_client(supabase_url, supabase_key)

# ---------------------------
# Extraction
# ---------------------------

def fetch_rss_feed(url: str) -> Optional[bytes]:
    """Fetch RSS feed content"""
    headers = {
        'User-Agent': 'Mozilla/5.0 (compatible; TenderScraper/1.0)',
        'Accept': 'application/rss+xml,application/xml;q=0.9,*/*;q=0.8'
    }
    
    try:
        response = requests.get(url, headers=headers, timeout=15)
        response.raise_for_status()
        return response.content
    except requests.exceptions.RequestException as e:
        logging.error(f'Failed to fetch RSS feed: {str(e)}')
        return None

# ---------------------------
# Transformation
# ---------------------------

class TenderTransformer:
    """Handles transformation of raw RSS data to tender objects"""
    
    @staticmethod
    def parse_xml(xml_content: bytes) -> List[Dict[str, Any]]:
        """Parse XML content into tender dictionaries"""
        try:
            root = ET.fromstring(xml_content)
            return [TenderTransformer._parse_item(item) for item in root.findall('.//channel/item')]
        except ET.ParseError as e:
            logging.error(f'XML parsing error: {str(e)}')
            return []

    @staticmethod
    def _parse_item(item) -> Dict[str, Any]:
        """Parse individual RSS item"""
        title = item.findtext('title', '').strip()
        description = item.findtext('description', '')
        pub_date = item.findtext('pubDate', '')
        link = item.findtext('link', '').split('#')[0]

        return {
            'raw_title': title,
            'raw_description': description,
            'pub_date': pub_date,
            'link': link,
            'deadline': TenderTransformer._extract_deadline(description),
            'region': TenderTransformer._extract_region(description),
            'category': TenderTransformer._categorize_tender(title, description),
            'clean_description': TenderTransformer._clean_description(description)
        }

    @staticmethod
    def _extract_deadline(description: str) -> datetime:
        """Extract deadline from description"""
        deadline_match = re.search(
            r'Angebotsfrist:\s*<strong>(\d{2}\.\d{2}\.\d{4})\s*(\d{2}:\d{2})?<\/strong>',
            description
        )
        if deadline_match:
            return parse_german_date(deadline_match.group(1), deadline_match.group(2))
        return datetime.now() + timedelta(days=30)

    @staticmethod
    def _extract_region(description: str) -> Optional[str]:
        """Extract region from description"""
        location_match = re.search(r'Erfüllungsort:\s*<strong>([^<]+)<\/strong>', description)
        return extract_region_from_location(location_match.group(1)) if location_match else None

    @staticmethod
    def _categorize_tender(title: str, description: str) -> Optional[str]:
        """Categorize tender based on allowed categories"""
        text = f'{title} {description}'.lower()
        for category, keywords in CONFIG['ALLOWED_CATEGORIES'].items():
            if any(re.search(rf'\b{re.escape(kw.lower())}\b', text) for kw in keywords):
                return category
        return None

    @staticmethod
    def _clean_description(description: str) -> str:
        """Clean HTML from description"""
        soup = BeautifulSoup(description, 'html.parser')
        text = soup.get_text(separator=' ', strip=True)
        return text.replace('&nbsp;', ' ').replace('\xa0', ' ')

# ---------------------------
# Loading
# ---------------------------

class TenderLoader:
    """Handles database operations for tenders"""
    
    def __init__(self, client: Client):
        self.client = client
    
    def process_tenders(self, tenders: List[Dict[str, Any]]) -> None:
        """Process and load filtered tenders"""
        valid_tenders = [t for t in tenders if t['category'] in CONFIG['ALLOWED_CATEGORIES']]
        logging.info(f'Processing {len(valid_tenders)}/{len(tenders)} filtered tenders')
        
        for tender in valid_tenders:
            try:
                if not self._tender_exists(tender['link']):
                    self._insert_tender(tender)
            except Exception as e:
                logging.error(f'Error processing tender {tender["link"]}: {str(e)}')

    def _tender_exists(self, url: str) -> bool:
        """Check if tender already exists in database"""
        response = self.client.table('tenders') \
            .select('id') \
            .eq('tender_url', url) \
            .execute()
        return len(response.data) > 0

    def _insert_tender(self, tender: Dict[str, Any]) -> None:
        """Insert new tender into database"""
        tender_data = {
            'title': tender['raw_title'],
            'description': tender['clean_description'],
            'publication_date': parse_rfc822_date(tender['pub_date']),
            'deadline': tender['deadline'].isoformat(),
            'category': tender['category'],
            'region': tender['region'],
            'estimated_value': extract_estimated_value(tender['clean_description']),
            'tender_url': tender['link'],
            'source': CONFIG['SOURCE_NAME'],
            'source_url': CONFIG['RSS_FEED'],
            'status': 'new'
        }
        
        result = self.client.table('tenders').insert(tender_data).execute()
        if result.data:
            logging.info(f'Inserted tender: {tender["raw_title"]}')
        else:
            logging.error(f'Failed to insert tender: {result.error}')

# ---------------------------
# Helper Functions
# ---------------------------

def parse_german_date(date_str: str, time_str: Optional[str] = None) -> datetime:
    """Parse German date format (DD.MM.YYYY)"""
    try:
        date_obj = datetime.strptime(date_str, '%d.%m.%Y')
        if time_str:
            time_obj = datetime.strptime(time_str, '%H:%M').time()
            return datetime.combine(date_obj, time_obj)
        return datetime.combine(date_obj, datetime.max.time())
    except ValueError:
        return datetime.now() + timedelta(days=30)

def parse_rfc822_date(date_str: str) -> str:
    """Parse RFC 822 date format"""
    try:
        return parse(date_str).isoformat()
    except (ValueError, TypeError):
        return datetime.now().isoformat()

def extract_region_from_location(location: str) -> Optional[str]:
    """Extract region from location string"""
    regions = [
        'Berlin', 'Hamburg', 'München', 'Köln', 'Frankfurt', 'Stuttgart',
        'Düsseldorf', 'Leipzig', 'Dortmund', 'Dresden', 'Hannover',
        'Nordrhein-Westfalen', 'Bayern', 'Baden-Württemberg', 'Niedersachsen',
        'Hessen', 'Rheinland-Pfalz', 'Sachsen', 'Thüringen', 'Brandenburg',
        'Sachsen-Anhalt', 'Schleswig-Holstein', 'Mecklenburg-Vorpommern',
        'Saarland', 'Bremen'
    ]
    return next((r for r in regions if r in location), None)

def extract_estimated_value(text: str) -> Optional[float]:
    """Extract estimated value from text"""
    patterns = [
        r'(?:geschätztes|voraussichtliches)\s*Auftragsvolumen:?\s*([\d.,]+)\s*(?:T€|Tsd|Tausend)',
        r'(?:geschätzter|voraussichtlicher)\s*Wert:?\s*([\d.,]+)\s*(?:T€|Tsd|Tausend)',
        r'Auftragswert:?\s*([\d.,]+)\s*(?:T€|Tsd|Tausend)'
    ]
    
    for pattern in patterns:
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            value = float(match.group(1).replace('.', '').replace(',', '.'))
            return value * 1000
    return None

# ---------------------------
# Pipeline Execution
# ---------------------------

def main():
    """Main ETL pipeline execution"""
    try:
        # Initialize clients
        supabase_client = get_supabase_client()
        loader = TenderLoader(supabase_client)
        
        # ETL Process
        logging.info('Starting ETL pipeline')
        
        # Extraction
        xml_content = fetch_rss_feed(CONFIG['RSS_FEED'])
        if not xml_content:
            return
            
        # Transformation
        raw_tenders = TenderTransformer.parse_xml(xml_content)
        
        # Loading
        loader.process_tenders(raw_tenders)
        
        logging.info('ETL pipeline completed successfully')
    except Exception as e:
        logging.error(f'Pipeline failed: {str(e)}')

if __name__ == '__main__':
    main()