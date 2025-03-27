##########################################################
# File: build-backend.sh                                 #
# Version: 1.0                                           #
# Author: Janis Grüttmüller on 11.03.2025                #
# Description: script to build the backend docker image  #
# of the LeanX ERP-System and push it to dockerhub       #
#                                                        #
# change history:                                        #
# 24.03.2025 - initial version                           #
##########################################################

#!/bin/bash
set -e  # Exit if any command fails

# Versioning Schema: Major.Minor.Patch
VERSION="v.1.0.0"

# Build backend with Maven
echo "Building backend with Maven..."
mvn clean package

# Docker login
echo "Logging in to Docker Hub..."
echo "$DOCKERHUB_ACCESS_TOKEN" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin

# Build backend Docker image
echo "Building backend Docker image..."
docker build --no-cache -t "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest" -t "$DOCKERHUB_USERNAME/leanx-erp-system-backend:$VERSION" -t "$DOCKERHUB_USERNAME/leanx-erp-system-backend:$VERSION-${GITHUB_SHA::7}" \
    -f backend/Dockerfile backend

# Push Docker images to Docker Hub
echo "Pushing Docker images to Docker Hub..."
docker push "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest"
docker push "$DOCKERHUB_USERNAME/leanx-erp-system-backend:$VERSION"
docker push "$DOCKERHUB_USERNAME/leanx-erp-system-backend:$VERSION-${GITHUB_SHA::7}"


echo "Build process completed successfully!"