export interface Tender {
    id: string;
    title: string;
    description: string;
    publicationDate: string;
    deadline: string;
    source: string;
    sourceUrl: string;
    tenderUrl: string;
    category?: string;
    region?: string;
    estimatedValue?: number;
    status: 'new' | 'active' | 'closed';
    isBookmarked?: boolean;
  }
  
  export interface TenderFilters {
    search?: string;
    category?: string;
    region?: string;
    status?: Tender['status'];
    dateRange?: {
      start: string;
      end: string;
    };
  }
  
  export interface TenderSource {
    name: string;
    url: string;
    type: 'rss' | 'api';
    lastFetched?: string;
  } 