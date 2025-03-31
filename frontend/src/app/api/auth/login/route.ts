import { NextResponse } from 'next/server'

export async function POST(request: Request) {
  try {
    let formData;
    
    // If the request content type is JSON, parse it as JSON
    if (request.headers.get('Content-Type')?.includes('application/json')) {
      const jsonData = await request.json();
      console.log('Attempting login with:', jsonData);

      formData = jsonData; // Treat jsonData as formData for consistency with your existing code
    } else {
      // If it's multipart/form-data, parse using formData
      formData = await request.formData();
      console.log('Attempting login with:', {
        username: formData.get('username'),
        url: 'http://leanx-backend:8080/api/auth/login'
      });
    }

    const username = formData?.username || formData?.get('username') as string;
    const password = formData?.password || formData?.get('password') as string;

    const jsonData = {
      username: username,
      password: password,
    };

    const response = await fetch('http://leanx-backend:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', // Correct content type for sending JSON
      },
      body: JSON.stringify(jsonData),    
    });

    console.log('Response status:', response.status);
    
    // Get response as text first
    const text = await response.text();
    console.log('Response text:', text);

    // Try to parse as JSON, if not return as text
    try {
      const json = JSON.parse(text);
      return NextResponse.json(json, { 
        status: response.status,
        headers: {
          'Content-Type': 'application/json'
        }
      });
    } catch (e) {
      return NextResponse.json({ 
        message: text 
      }, { 
        status: response.status,
        headers: {
          'Content-Type': 'application/json'
        }
      });
    }
  } catch (error) {
    console.error('Login error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}