import { NextResponse } from 'next/server'

export async function POST(request: Request) {
  try {
    const formData = await request.formData()
    
    console.log('Attempting login with:', {
      username: formData.get('username'),
      url: 'http://16.16.234.230:80/backend-1.0-SNAPSHOT/api/auth/login'
    })

    const response = await fetch('http://16.16.234.230:80/backend-1.0-SNAPSHOT/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      body: new URLSearchParams({
        username: formData.get('username') as string,
        password: formData.get('password') as string,
      }).toString()
    })

    console.log('Response status:', response.status)
    
    // Get response as text first
    const text = await response.text()
    console.log('Response text:', text)

    // Try to parse as JSON, if not return as text
    try {
      const json = JSON.parse(text)
      return NextResponse.json(json, { 
        status: response.status,
        headers: {
          'Content-Type': 'application/json'
        }
      })
    } catch (e) {
      return NextResponse.json({ 
        message: text 
      }, { 
        status: response.status,
        headers: {
          'Content-Type': 'application/json'
        }
      })
    }
  } catch (error) {
    console.error('Login error:', error)
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    )
  }
} 