/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://backend-server:8080/api/:path*'
      }
    ]
  }
}

module.exports = nextConfig 