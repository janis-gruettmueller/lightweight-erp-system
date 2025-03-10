#!/bin/bash

# Checkout code
git checkout main

# Set up JDK 17 for backend
echo "Setting up JDK 17..."
sudo apt update
sudo apt install -y openjdk-17-jdk

# Build backend with Maven
echo "Building backend with Maven..."
mvn clean package

# Docker login
echo "Logging in to Docker Hub..."
echo "$DOCKERHUB_ACCESS_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

# Build backend Docker image
echo "Building backend Docker image..."
docker build -t "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest" -t "$DOCKERHUB_USERNAME/leanx-erp-system-backend:${GITHUB_RUN_NUMBER}" \
    --build-arg JWT_SECRET_KEY="$JWT_SECRET_KEY" \
    --build-arg RDS_MYSQL_USER="$RDS_MYSQL_USER" \
    --build-arg RDS_MYSQL_PASSWORD="$RDS_MYSQL_PASSWORD" \
    --build-arg RDS_MYSQL_ENDPOINT="$RDS_MYSQL_ENDPOINT" \
    --build-arg RDS_MYSQL_PORT="$RDS_MYSQL_PORT" \
    -f backend/Dockerfile backend

# Build frontend Docker image
# echo "Building frontend Docker image..."
# docker build -t "$DOCKERHUB_USERNAME/leanx-erp-system-frontend:latest" -t "$DOCKERHUB_USERNAME/leanx-erp-system-frontend:${GITHUB_RUN_NUMBER}" \
#     -f frontend/Dockerfile frontend

# Push Docker images to Docker Hub
echo "Pushing Docker images to Docker Hub..."
docker push "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest"
docker push "$DOCKERHUB_USERNAME/leanx-erp-system-backend:${GITHUB_RUN_NUMBER}"
# docker push "$DOCKERHUB_USERNAME/leanx-erp-system-frontend:latest"
# docker push "$DOCKERHUB_USERNAME/leanx-erp-system-frontend:${GITHUB_RUN_NUMBER}"

echo "Build process completed successfully!"