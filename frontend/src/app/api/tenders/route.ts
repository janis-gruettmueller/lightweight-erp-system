import { createClient } from '@supabase/supabase-js'
import { NextResponse } from 'next/server'

// wenn möglich noch anpassen mit .env file
const supabaseUrl = 'https://iwdpjibascqkzjtkqowq.supabase.co';
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml3ZHBqaWJhc2Nxa3pqdGtxb3dxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDI4MTk5NjAsImV4cCI6MjA1ODM5NTk2MH0.WYXE5DkQ8exRQQd94X6o43TGcrC6_j34FGEqmxNVeAM';
const supabase = createClient(supabaseUrl, supabaseAnonKey)

// Define valid sort fields and their corresponding database columns
const VALID_SORT_FIELDS = {
  deadline: 'deadline'
} as const

// This is a mock implementation. In a real application, you would:
// 1. Connect to your database
// 2. Implement proper filtering and pagination
// 3. Add error handling
export async function GET(request: Request) {
  try {
    const { searchParams } = new URL(request.url)
    const search = searchParams.get('search')
    const category = searchParams.get('category')
    const sortBy = searchParams.get('sortBy') || 'deadline'
    const sortOrder = searchParams.get('sortOrder') || 'asc'
    const page = parseInt(searchParams.get('page') || '1')
    const limit = parseInt(searchParams.get('limit') || '20')

    // Validate sort parameters
    if (!Object.values(VALID_SORT_FIELDS).includes(sortBy as any)) {
      return new Response(JSON.stringify({ error: 'Invalid sort field' }), {
        status: 400,
        headers: { 'Content-Type': 'application/json' },
      })
    }

    if (!['asc', 'desc'].includes(sortOrder.toLowerCase())) {
      return new Response(JSON.stringify({ error: 'Invalid sort order' }), {
        status: 400,
        headers: { 'Content-Type': 'application/json' },
      })
    }

    let query = supabase
      .from('tenders')
      .select('*', { count: 'exact' })

    // Apply filters only if they are explicitly set
    if (search && search.trim() !== '') {
      query = query.or(`title.ilike.%${search}%,description.ilike.%${search}%`)
    }
    if (category) {
      // Use exact matching for categories
      query = query.eq('category', category === 'it' ? 'IT' : 
        category === 'it_digitalisierung' ? 'IT & Digitalisierung' :
        category === 'bauarbeiten' ? 'Bauarbeiten' :
        category === 'dienstleistungen' ? 'Dienstleistungen' :
        category === 'facility_management' ? 'Facility Management' :
        category === 'infrastruktur' ? 'Infrastruktur' :
        category === 'lieferungen' ? 'Lieferungen' :
        category === 'planung_beratung' ? 'Planung & Beratung' :
        category)
    }

    // Apply sorting with validated fields
    query = query.order(sortBy, { ascending: sortOrder === 'asc' })

    // Apply pagination
    const start = (page - 1) * limit
    query = query.range(start, start + limit - 1)

    const { data: tenders, error, count } = await query

    if (error) {
      console.error('Database error:', error)
      return NextResponse.json(
        { error: 'Database error occurred' },
        { status: 500 }
      )
    }

    // Get unique categories for filters
    const { data: categories } = await supabase
      .from('tenders')
      .select('category')
      .not('category', 'is', null)
      .order('category')

    // Get total IT tenders count
    const { count: itCount } = await supabase
      .from('tenders')
      .select('*', { count: 'exact' })
      .in('category', ['IT', 'IT & Digitalisierung'])

    return NextResponse.json({
      tenders,
      total: count,
      page,
      totalPages: Math.ceil((count || 0) / limit),
      limit,
      totalITTenders: itCount,
      filters: {
        categories: [...new Set(categories?.map(c => c.category) || [])]
      }
    })
  } catch (error) {
    console.error('Error fetching tenders:', error)
    return NextResponse.json(
      { error: 'Failed to fetch tenders' },
      { status: 500 }
    )
  }
}

// Handle bookmarking
export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { tenderId, action } = body;

    if (action === 'bookmark') {
      const { error } = await supabase
        .from('tenders')
        .update({ is_bookmarked: true })
        .eq('id', tenderId);

      if (error) {
        console.error('Supabase error:', error);
        return NextResponse.json(
          { error: 'Database error occurred' },
          { status: 500 }
        );
      }
    }

    return NextResponse.json({ success: true });
  } catch (error) {
    console.error('API error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
} 