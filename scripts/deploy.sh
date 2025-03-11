##########################################################
# File: deploy.sh                                        #
# Version: 1.0                                           #
# Author: Janis Grüttmüller on 11.03.2025                #
# Description: script to deploy the erp-system on a AWS  #
# EC2 Instance                                           #
#                                                        #
# change history:                                        #
# 11.03.2025 - initial version                           #
##########################################################

#!/bin/bash
set -e  # Exit if any command fails

# Configuration Variables
TEMP_SSH_KEY="AWS_EC2_ACCESS_KEY_TEMP.pem"
DOCKER_COMPOSE_FILE="docker-compose.yml"

# Step 1: Save GitHub Secret (SSH Key) to a File & Set Permissions
cat "$AWS_EC2_ACCESS_KEY" > $TEMP_SSH_KEY
chmod 600 $TEMP_SSH_KEY  # Secure the key file

# Step 2: Copy current docker-compose.yml file to EC2 and replace old file
echo "Copying files to EC2 instance..."
scp -i $EC2_SSH_KEY -o StrictHostKeyChecking=no $DOCKER_COMPOSE_FILE $EC2_USER@$EC2_PUBLIC_IP:/home/ubuntu/

# Step 3: SSH into EC2 and deploy with docker-compose
echo "Connecting to EC2 instance and deploying images with docker-compose..."
ssh -i $TEMP_SSH_KEY -o StrictHostKeyChecking=no $EC2_USER@$EC2_PUBLIC_IP << EOF
  cd /home/ubuntu
  
  echo "Pulling latest Docker images..."
  docker-compose pull

  echo "Stopping old containers..."
  docker-compose down --remove-orphans  # Keeps named volumes intact

  echo "Starting new containers..."
  docker-compose up -d

  echo "Deployment completed!"
EOF

# Step 4: Cleanup SSH Key
rm -f $TEMP_SSH_KEY

echo "Successfully Deployed!"