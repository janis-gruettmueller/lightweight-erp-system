import os
import requests
import xml.etree.ElementTree as ET
import psycopg2
from datetime import datetime
from bs4 import BeautifulSoup
from dotenv import load_dotenv

# Load environment variables
load_dotenv()
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY")
DATABASE_URL = os.getenv("DATABASE_URL")  # PostgreSQL connection string

RSS_FEED_URL = "https://www.service.bund.de/IMPORTE/RSS/Extern/Ausschreibungen/Standard/Anzeigen/RSSGenerator_Ausschreibungen.xml"

# Connect to PostgreSQL (Supabase uses PostgreSQL under the hood)
def connect_db():
    return psycopg2.connect(DATABASE_URL)

def fetch_rss_feed():
    response = requests.get(RSS_FEED_URL)
    response.raise_for_status()
    return response.text

def parse_rss_feed(xml_data):
    root = ET.fromstring(xml_data)
    namespace = {'ns': 'http://purl.org/rss/1.0/'}
    items = root.findall(".//ns:item", namespace)
    
    tenders = []
    for item in items:
        title = item.find("ns:title", namespace).text if item.find("ns:title", namespace) is not None else ""
        description_raw = item.find("ns:description", namespace).text if item.find("ns:description", namespace) is not None else ""
        link = item.find("ns:link", namespace).text if item.find("ns:link", namespace) is not None else ""
        
        description = BeautifulSoup(description_raw, "html.parser").get_text()
        
        pub_date = item.find("ns:pubDate", namespace).text if item.find("ns:pubDate", namespace) is not None else ""
        deadline = extract_deadline(description)
        
        tenders.append({
            "title": title,
            "description": description,
            "publication_date": parse_date(pub_date),
            "deadline": deadline,
            "url": link
        })
    
    return tenders

def parse_date(date_str):
    try:
        return datetime.strptime(date_str, "%a, %d %b %Y %H:%M:%S %Z")
    except Exception:
        return None

def extract_deadline(description):
    try:
        for line in description.split("\n"):
            if "Deadline:" in line:
                date_str = line.split("Deadline:")[-1].strip()
                return datetime.strptime(date_str, "%d.%m.%Y").date()
    except Exception:
        return None

def store_tenders(tenders):
    conn = connect_db()
    cursor = conn.cursor()
    
    for tender in tenders:
        cursor.execute("SELECT 1 FROM tenders WHERE url = %s", (tender["url"],))
        exists = cursor.fetchone()
        
        if not exists:
            cursor.execute("""
                INSERT INTO tenders (title, description, publication_date, deadline, url, status)
                VALUES (%s, %s, %s, %s, %s, %s)
            """, (tender["title"], tender["description"], tender["publication_date"], tender["deadline"], tender["url"], "new"))
    
    conn.commit()
    cursor.close()
    conn.close()

def main():
    print("Fetching tenders...")
    xml_data = fetch_rss_feed()
    tenders = parse_rss_feed(xml_data)
    print(f"Fetched {len(tenders)} tenders. Storing in database...")
    store_tenders(tenders)
    print("Done!")

if __name__ == "__main__":
    main()
