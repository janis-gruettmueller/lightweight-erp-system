/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://16.16.234.230:80/backend-1.0-SNAPSHOT/api/:path*'
      }
    ]
  }
}

module.exports = nextConfig 