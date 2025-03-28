import { createClient } from '@supabase/supabase-js'
import { NextResponse } from 'next/server'

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL!
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
const supabase = createClient(supabaseUrl, supabaseAnonKey)

// This is a mock implementation. In a real application, you would:
// 1. Connect to your database
// 2. Implement proper filtering and pagination
// 3. Add error handling
export async function GET(request: Request) {
  try {
    const { searchParams } = new URL(request.url)
    const search = searchParams.get('search')
    const category = searchParams.get('category')
    const region = searchParams.get('region')
    const status = searchParams.get('status')
    const sortBy = searchParams.get('sortBy') || 'publication_date'
    const sortOrder = searchParams.get('sortOrder') || 'desc'
    const page = parseInt(searchParams.get('page') || '1')
    const limit = parseInt(searchParams.get('limit') || '20') // Increased default limit

    let query = supabase
      .from('tenders')
      .select('*', { count: 'exact' })

    // Apply filters only if they are explicitly set
    if (search && search.trim() !== '') {
      query = query.or(`title.ilike.%${search}%,description.ilike.%${search}%`)
    }
    if (category && category !== 'all') {
      query = query.eq('category', category)
    }
    if (region && region !== 'all') {
      query = query.eq('region', region)
    }
    if (status && status !== 'all') {
      query = query.eq('status', status)
    }

    // Apply sorting
    query = query.order(sortBy, { ascending: sortOrder === 'asc' })

    // Apply pagination
    const start = (page - 1) * limit
    query = query.range(start, start + limit - 1)

    const { data: tenders, error, count } = await query

    if (error) {
      throw error
    }

    // Get unique categories and regions for filters
    const { data: categories } = await supabase
      .from('tenders')
      .select('category')
      .not('category', 'is', null)
      .order('category')

    const { data: regions } = await supabase
      .from('tenders')
      .select('region')
      .not('region', 'is', null)
      .order('region')

    return NextResponse.json({
      tenders,
      total: count,
      page,
      totalPages: Math.ceil((count || 0) / limit),
      limit,
      filters: {
        categories: [...new Set(categories?.map(c => c.category) || [])],
        regions: [...new Set(regions?.map(r => r.region) || [])]
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