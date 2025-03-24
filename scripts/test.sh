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

# Step 2: Run the backend Docker container
echo "Starting backend Docker container..."
docker run -d --name backend-test "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest"
sleep 5  # Wait for the container to start

# Step 3: Verify if the containers are running
if ! docker ps --filter "name=backend-test" --filter "status=running" | grep backend-test; then
  echo "🛑 ERROR: Backend container failed to start or crashed"
  exit 1
fi

# Step 4: Check logs for errors (backend)
if docker logs backend-test | grep -i "error"; then
  echo "🛑 ERROR: Backend application logs contain errors"
  exit 1
fi


echo "✅ Tests passed successfully!"