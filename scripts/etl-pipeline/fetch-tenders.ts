import { createClient } from '@supabase/supabase-js';
import { config } from 'dotenv';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import axios from 'axios';
import { XMLParser } from 'fast-xml-parser';
import * as cheerio from 'cheerio';

// Load environment variables from .env.local
const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
config({ path: join(__dirname, '../../.env.local') });

// Verify environment variables
const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL;
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY;

if (!supabaseUrl || !supabaseAnonKey) {
  throw new Error('Missing Supabase environment variables. Please check your .env.local file.');
}

const supabase = createClient(supabaseUrl, supabaseAnonKey);
const xmlParser = new XMLParser();

// service.bund.de RSS feed URL
const TENDER_SOURCE = {
  name: 'service.bund.de Tenders',
  rssUrl: 'https://www.service.bund.de/Content/Globals/Functions/RSSFeed/RSSGenerator_Ausschreibungen.xml',
  type: 'rss' as const,
};

interface TenderData {
  title: string;
  description: string;
  publication_date: string;
  deadline: Date;
  category: string | null;
  region: string | null;
  estimated_value: number | null;
  tender_url?: string;
  source?: string;
  source_url?: string;
  status?: string;
}

async function checkDatabase() {
  try {
    console.log('Checking database structure and data...');
    
    // Check if the tenders table exists and get its structure
    const { data: tableInfo, error: tableError } = await supabase
      .from('tenders')
      .select('*')
      .limit(1);
    
    if (tableError) {
      console.error('Error accessing tenders table:', tableError);
      return;
    }
    
    // Get count of existing tenders
    const { count, error: countError } = await supabase
      .from('tenders')
      .select('*', { count: 'exact', head: true });
    
    if (countError) {
      console.error('Error counting tenders:', countError);
      return;
    }
    
    console.log('Database check results:', {
      tableExists: true,
      totalTenders: count,
      sampleData: tableInfo ? 'Available' : 'No data'
    });
    
    return true;
  } catch (error) {
    console.error('Error checking database:', error);
    return false;
  }
}

export async function fetchAndStoreTenders() {
  try {
    console.log('Starting tender fetch process...');
    console.log(`Supabase URL: ${supabaseUrl}`);
    
    // First check the database
    const dbCheck = await checkDatabase();
    if (!dbCheck) {
      console.error('Database check failed. Please verify your database setup.');
      return;
    }
    
    console.log('Starting tender fetch process...');
    console.log(`Supabase URL: ${supabaseUrl}`);
    console.log(`Target URL: ${TENDER_SOURCE.rssUrl}`);
    
    // First, check if we can connect to Supabase
    console.log('Testing Supabase connection...');
    const { data: testData, error: testError } = await supabase
      .from('tenders')
      .select('count')
      .limit(1);
    
    if (testError) {
      console.error('Supabase connection error:', testError);
      return;
    }
    console.log('Supabase connection successful');

    // Fetch RSS feed
    console.log('Fetching RSS feed...');
    const response = await axios.get(TENDER_SOURCE.rssUrl, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (compatible; LeanX-ERP/1.0; +http://leanx-erp.com)',
        'Accept': 'application/rss+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7'
      },
      timeout: 10000,
      maxRedirects: 5,
      validateStatus: (status) => status < 400
    });

    if (!response.data) {
      console.warn('No data received from RSS feed');
      return;
    }

    console.log('RSS feed response:', {
      status: response.status,
      contentType: response.headers['content-type'],
      dataLength: response.data?.length
    });

    // Parse XML response
    console.log('Parsing RSS feed...');
    const parsedXml = xmlParser.parse(response.data);
    console.log('Parsed XML structure:', JSON.stringify(parsedXml, null, 2));
    
    const items = parsedXml.rss?.channel?.item || [];
    console.log(`Found ${items.length} items in RSS feed`);

    // Process each tender
    for (const item of items) {
      try {
        const tender = extractTenderFromRSS(item);
        console.log('\nProcessing tender:', { title: tender.title });

        // Check if tender already exists
        const { data: existingTender, error: checkError } = await supabase
          .from('tenders')
          .select('id')
          .eq('tender_url', tender.tender_url)
          .single();

        if (checkError && checkError.code !== 'PGRST116') { // PGRST116 is "not found"
          console.error('Error checking for existing tender:', checkError);
          continue;
        }

        if (existingTender) {
          console.log('Tender already exists, skipping...');
          continue;
        }

        // Add source information
        tender.source = TENDER_SOURCE.name;
        tender.source_url = TENDER_SOURCE.rssUrl;
        tender.status = 'new';

        console.log('Inserting tender data:', tender);

        // Insert new tender
        const { error: insertError } = await supabase
          .from('tenders')
          .insert(tender);

        if (insertError) {
          console.error('Error inserting tender:', insertError);
        } else {
          console.log('Successfully inserted tender');
        }
      } catch (error) {
        console.error('Error processing RSS item:', error);
      }
    }

    console.log('\nFinished processing all tenders');
  } catch (error) {
    console.error('Error in fetchAndStoreTenders:', error);
    if (axios.isAxiosError(error)) {
      console.error('Axios error details:', {
        message: error.message,
        code: error.code,
        response: error.response?.data,
        status: error.response?.status
      });
    }
  }
}

function extractTenderFromRSS(item: any): TenderData {
  // Extract deadline from description using regex
  const deadlineMatch = item.description?.match(/Angebotsfrist:\s*<strong>(\d{2}\.\d{2}\.\d{4})\s*(\d{2}:\d{2})?<\/strong>/);
  const deadline = deadlineMatch 
    ? parseGermanDate(deadlineMatch[1], deadlineMatch[2])
    : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000); // Default to 30 days from now

  // Extract location/region from description
  const locationMatch = item.description?.match(/Erfüllungsort:\s*<strong>([^<]+)<\/strong>/);
  const region = locationMatch ? extractRegionFromLocation(locationMatch[1]) : null;

  // Clean up description by removing HTML tags and converting entities
  const cleanDescription = item.description
    ? item.description
        .replace(/<[^>]+>/g, ' ')
        .replace(/&nbsp;/g, ' ')
        .replace(/&auml;/g, 'ä')
        .replace(/&uuml;/g, 'ü')
        .replace(/&ouml;/g, 'ö')
        .replace(/&Auml;/g, 'Ä')
        .replace(/&Uuml;/g, 'Ü')
        .replace(/&Ouml;/g, 'Ö')
        .replace(/&szlig;/g, 'ß')
        .replace(/\s+/g, ' ')
        .trim()
    : '';

  return {
    title: item.title || '',
    description: cleanDescription,
    publication_date: new Date(item.pubDate).toISOString(),
    deadline,
    category: extractCategory(item.title, cleanDescription),
    region,
    estimated_value: extractEstimatedValue(cleanDescription),
    tender_url: item.link?.replace(/#track=feed-callforbids$/, '') || '',
  };
}

function parseGermanDate(dateStr: string, timeStr?: string): Date {
  const [day, month, year] = dateStr.split('.').map(Number);
  const date = new Date(year, month - 1, day);
  
  if (timeStr) {
    const [hours, minutes] = timeStr.split(':').map(Number);
    date.setHours(hours, minutes);
  } else {
    date.setHours(23, 59, 59);
  }
  
  return date;
}

function extractRegionFromLocation(location: string): string | null {
  // List of German states and major cities
  const regions = [
    'Berlin', 'Hamburg', 'München', 'Köln', 'Frankfurt', 'Stuttgart',
    'Düsseldorf', 'Leipzig', 'Dortmund', 'Dresden', 'Hannover',
    'Nordrhein-Westfalen', 'Bayern', 'Baden-Württemberg', 'Niedersachsen',
    'Hessen', 'Rheinland-Pfalz', 'Sachsen', 'Thüringen', 'Brandenburg',
    'Sachsen-Anhalt', 'Schleswig-Holstein', 'Mecklenburg-Vorpommern',
    'Saarland', 'Bremen'
  ];

  // First try to match postal code and city
  const postalMatch = location.match(/\b(\d{5})\s+([A-ZÄÖÜß][a-zäöüß-]+(?:\s+[A-ZÄÖÜß][a-zäöüß-]+)*)\b/);
  if (postalMatch) {
    return postalMatch[2];
  }

  // Then try to match known regions
  for (const region of regions) {
    if (location.includes(region)) {
      return region;
    }
  }

  return null;
}

function extractCategory(title: string, description: string): string | null {
  const categories = {
    'IT & Digitalisierung': ['IT', 'Software', 'Hardware', 'Systeme', 'Datenbank', 'Netzwerk', 'Digital'],
    'Bauarbeiten': ['Bau', 'Sanierung', 'Renovierung', 'Modernisierung', 'Neubau', 'Umbau'],
    'Dienstleistungen': ['Dienstleistung', 'Service', 'Beratung', 'Wartung', 'Pflege', 'Reinigung'],
    'Lieferungen': ['Lieferung', 'Beschaffung', 'Ausstattung', 'Möbel', 'Material'],
    'Planung & Beratung': ['Planung', 'Konzept', 'Studie', 'Gutachten', 'Beratung'],
    'Infrastruktur': ['Straße', 'Kanal', 'Verkehr', 'Infrastruktur', 'Netz'],
    'Facility Management': ['Facility', 'Gebäude', 'Instandhaltung', 'Wartung', 'Betrieb'],
  };

  const text = `${title} ${description}`.toLowerCase();
  
  for (const [category, keywords] of Object.entries(categories)) {
    if (keywords.some(keyword => text.includes(keyword.toLowerCase()))) {
      return category;
    }
  }

  return null;
}

function extractEstimatedValue(text: string): number | null {
  const valuePatterns = [
    /(?:geschätztes|voraussichtliches)\s*Auftragsvolumen:?\s*(\d+(?:[\.,]\d+)?)\s*(?:Tausend|Tsd\.?|T€|T\s*EUR|[kK]€|[kK]\s*EUR)/i,
    /(?:geschätzter|voraussichtlicher)\s*Wert:?\s*(\d+(?:[\.,]\d+)?)\s*(?:Tausend|Tsd\.?|T€|T\s*EUR|[kK]€|[kK]\s*EUR)/i,
    /Auftragswert:?\s*(\d+(?:[\.,]\d+)?)\s*(?:Tausend|Tsd\.?|T€|T\s*EUR|[kK]€|[kK]\s*EUR)/i,
  ];

  for (const pattern of valuePatterns) {
    const match = text.match(pattern);
    if (match) {
      const [_, value] = match;
      return parseFloat(value.replace(',', '.')) * 1000;
    }
  }

  return null;
}

// Run the script
fetchAndStoreTenders()
  .then(() => console.log('Script completed successfully'))
  .catch(error => console.error('Script failed:', error)); 