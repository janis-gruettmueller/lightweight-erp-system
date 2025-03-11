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

# Step 1: Pull latest image from DockerHub
echo "Pulling latest Docker image..."
docker pull "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest" || { echo "🛑 ERROR: Failed to pull Docker image"; exit 1; }

# Step 2: Run the Docker container
echo "Starting Docker container..."
docker run -d --name app-test "$DOCKERHUB_USERNAME/leanx-erp-system-backend:latest"
sleep 5  # Wait for the container to start

# Step 4: Verify if the container is running
if ! docker ps --filter "name=app-test" --filter "status=running" | grep app-test; then
  echo "🛑 ERROR: Container failed to start or crashed"
  exit 1
fi

# Step 5: Check logs for errors
if docker logs app-test | grep -i "error"; then
  echo "🛑 ERROR: Application logs contain errors"
  exit 1
fi

echo "✅ Tests passed successfully!"