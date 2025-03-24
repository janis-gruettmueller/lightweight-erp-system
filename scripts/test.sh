##########################################################
# File: test.sh                                          #
# Version: 1.0                                           #
# Author: Janis Grüttmüller on 11.03.2025                #
# Description: script to test the build of the           #
# docker image of the LeanX ERP-System                   #
#                                                        #
# change history:                                        #
# 11.03.2025 - initial version                           #
##########################################################

#!/bin/bash

set -e  # Exit script if any command fails

# Step 1: Pull latest images from DockerHub
echo "Pulling latest Docker images..."
docker pull "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest" || { echo "🛑 ERROR: Failed to pull backend Docker image"; exit 1; }
docker pull "$DOCKERHUB_USERNAME/leanx-erp-system-frontend:latest" || { echo "🛑 ERROR: Failed to pull frontend Docker image"; exit 1; }

# Step 2: Run the backend Docker container
echo "Starting backend Docker container..."
docker run -d --name backend-test "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest"
sleep 5  # Wait for the container to start

# Step 3: Run the frontend Docker container
echo "Starting frontend Docker container..."
docker run -d --name frontend-test -p 8080:80 "$DOCKERHUB_USERNAME/leanx-erp-system-frontend:latest"
sleep 5  # Wait for the container to start

# Step 4: Verify if the containers are running
if ! docker ps --filter "name=backend-test" --filter "status=running" | grep backend-test; then
  echo "🛑 ERROR: Backend container failed to start or crashed"
  exit 1
fi

if ! docker ps --filter "name=frontend-test" --filter "status=running" | grep frontend-test; then
  echo "🛑 ERROR: Frontend container failed to start or crashed"
  exit 1
fi

# Step 5: Check logs for errors (backend)
if docker logs backend-test | grep -i "error"; then
  echo "🛑 ERROR: Backend application logs contain errors"
  exit 1
fi

# Step 6: Check logs for errors (frontend)
if docker logs frontend-test | grep -i "error"; then
  echo "🛑 ERROR: Frontend application logs contain errors"
  exit 1
fi

echo "✅ Tests passed successfully!"