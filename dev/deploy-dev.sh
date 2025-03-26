#!/bin/bash

set -e

# Navigate to the directory containing docker-compose.yml
cd "$(dirname "$0")"

echo "Starting development environment..."

# removing old container
docker-compose down --remove-orphans

# Build and start the Docker Compose stack
docker-compose up --build -d

echo "Waiting for services to start..."
sleep 10  # Give services some time to start

# Check if containers are running
if docker ps --filter "name=leanx-web-server" | grep -q "leanx-web-server"; then
  echo "Nginx (leanx-web-server) is running."
else
  echo "Error: Nginx (leanx-web-server) failed to start."
  exit 1
fi

if docker ps --filter "name=leanx-frontend" | grep -q "leanx-frontend"; then
  echo "Frontend (leanx-frontend) is running."
else
  echo "Error: Frontend (leanx-frontend) failed to start."
  exit 1
fi

if docker ps --filter "name=leanx-backend" | grep -q "leanx-backend"; then
  echo "Backend (leanx-backend) is running."
else
  echo "Error: Backend (leanx-backend) failed to start."
  exit 1
fi

if docker ps --filter "name=leanx-db" | grep -q "leanx-db"; then
  echo "Database (leanx-db) is running."
else
  echo "Error: Database (leanx-db) failed to start."
  exit 1
fi

echo "Development environment started!"
echo "------------------------------------"
echo "Access the application at: http://localhost:80"
echo "------------------------------------"