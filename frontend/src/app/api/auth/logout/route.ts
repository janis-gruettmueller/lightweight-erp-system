import { NextResponse } from 'next/server'

export async function POST(request: Request) {
  try {
    const response = await fetch('http://backend-server:8080/api/auth/logout', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      credentials: 'include'
    })

    // Log the response for debugging
    console.log('Logout response status:', response.status);
    const responseText = await response.text();
    console.log('Logout response text:', responseText);

    // Even if we get a different status code, we want to log the user out
    // of the frontend anyway
    return NextResponse.json(
      { message: 'Logged out successfully' },
      { status: 200 }
    )
  } catch (error) {
    console.error('Logout error details:', error);
    // Still return success as we want to log out the frontend
    return NextResponse.json(
      { message: 'Logged out from frontend' },
      { status: 200 }
    )
  }
} 