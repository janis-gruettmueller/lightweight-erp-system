#!/bin/bash

# Load environment variables from .env file
ENV_FILE="./.env"
if [ -f "$ENV_FILE" ]; then
  export $(grep -v '^#' "$ENV_FILE" | xargs)
fi

# Configuration (using environment variables)
IMAGE_NAME="leanx"
CONTAINER_NAME="leanx-backend"
PORT=${PORT:-8080} # Use default if PORT is not set
DOCKERFILE_PATH="$HOME/leanx-system-root/leanx-erp-system/backend/Dockerfile"
PROJECT_ROOT="$(dirname $DOCKERFILE_PATH)"
WAR_FILE="backend/target/backend-1.0-SNAPSHOT.war"
WAR_NAME=$(basename "$WAR_FILE" .war)

# Ensure Docker is running
echo "🔍 Checking if Docker is running..."
if ! docker info >/dev/null 2>&1; then
    echo "🚀 Starting Docker..."
    open --background -a Docker  # macOS
    echo "⏳ Waiting for Docker to start..."
    while ! docker info >/dev/null 2>&1; do sleep 2; done
    echo "✅ Docker is now running!"
fi

# Build the WAR file first
echo "🔨 Building the WAR file..."
cd "$PROJECT_ROOT" || { echo "❌ Failed to find project root"; exit 1; }
mvn clean package || { echo "❌ Failed to build the WAR file"; exit 1; }

# Ensure the WAR file exists
WAR_FILE="target/backend-1.0-SNAPSHOT.war"
if [ ! -f "$WAR_FILE" ]; then
    echo "❌ ERROR: WAR file not found: $WAR_FILE"
    exit 1
fi

# Stop and remove existing container
echo "🔄 Stopping and removing any existing container..."
docker stop $CONTAINER_NAME >/dev/null 2>&1
docker rm $CONTAINER_NAME >/dev/null 2>&1

# Build the Docker image
echo "📦 Building Docker image: $IMAGE_NAME..."
docker build -t $IMAGE_NAME -f "$DOCKERFILE_PATH" "$PROJECT_ROOT"

# Run the container (modified for local MySQL)
echo "🚀 Running the container: $CONTAINER_NAME..."
docker run -d -p $PORT:8080 --name $CONTAINER_NAME \
    -e RDS_MYSQL_PASSWORD=$MYSQL_PASSWORD \
    -e RDS_MYSQL_USER=$MYSQL_USER \
    -e RDS_MYSQL_DB_NAME=$MYSQL_DB_NAME \
    -e RDS_MYSQL_ENDPOINT=host.docker.internal \
    -e RDS_MYSQL_PORT=3306 \
    $IMAGE_NAME

# Verify running container
echo "🔍 Checking running containers..."
docker ps | grep $CONTAINER_NAME

# Show API URL
echo "✅ Tomcat is running! Access your API at:"
echo "👉 http://localhost:$PORT/$WAR_NAME/api/*"

# Tail logs
echo "📜 Viewing Tomcat logs (Press Ctrl+C to exit)..."
docker logs -f $CONTAINER_NAME