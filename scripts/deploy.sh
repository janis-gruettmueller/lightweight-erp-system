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
TEMP_SSH_KEY=AWS_EC2_ACCESS_KEY_TEMP.pem
DOCKER_COMPOSE_FILE=docker-compose.yml
NGINX_CONF_FILE=web-server/nginx.conf
REVERSE_PROXY_CONF_FILE=web-server/reverse-proxy.conf
ENV_FILE=.env

# Step 1: Save GitHub Secret (SSH Key) to a File & Set Permissions
printf "%s" "$AWS_EC2_ACCESS_KEY" > $TEMP_SSH_KEY
chmod 600 $TEMP_SSH_KEY  # Secure the key file

# Step 2: Create .env file with GitHub Action Secrets and Variables
echo "Creating .env file with environment variables..."
echo "DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME}" >> $ENV_FILE
echo "EC2_ELASTIC_IP=${EC2_ELASTIC_IP}" >> $ENV_FILE
echo "RDS_MYSQL_USER=${RDS_MYSQL_USER}" >> $ENV_FILE
echo "RDS_MYSQL_PASSWORD=${RDS_MYSQL_PASSWORD}" >> $ENV_FILE
echo "RDS_MYSQL_ENDPOINT=${RDS_MYSQL_ENDPOINT}" >> $ENV_FILE
echo "RDS_MYSQL_PORT=${RDS_MYSQL_PORT}" >> $ENV_FILE
echo "RDS_MYSQL_DB_NAME=${RDS_MYSQL_DB_NAME}" >> $ENV_FILE
echo "SMTP_USERNAME=${SMTP_USERNAME}" >> $ENV_FILE
echo "SMTP_PASSWORD=${SMTP_PASSWORD}" >> $ENV_FILE
echo "SMTP_HOST=${SMTP_HOST}" >> $ENV_FILE
echo "NEXT_PUBLIC_SUPABASE_URL=${NEXT_PUBLIC_SUPABASE_URL}" >> $ENV_FILE
echo "NEXT_PUBLIC_SUPABASE_ANON_KEY=${NEXT_PUBLIC_SUPABASE_ANON_KEY}" >> $ENV_FILE

# Step 3: Copy .env and docker-compose.yml to EC2
echo "Copying .env, docker-compose.yml and nginx config files to EC2 instance..."
scp -i $TEMP_SSH_KEY -o StrictHostKeyChecking=no $DOCKER_COMPOSE_FILE $ENV_FILE $NGINX_CONF_FILE $REVERSE_PROXY_CONF_FILE $EC2_USER@$EC2_ELASTIC_IP:/home/ubuntu/

# Step 4: SSH into EC2 and deploy with docker-compose
echo "Connecting to EC2 instance and deploying images with docker-compose..."
ssh -i $TEMP_SSH_KEY -o StrictHostKeyChecking=no $EC2_USER@$EC2_ELASTIC_IP << EOF
  cd /home/ubuntu

  echo "Updating system packages..."
  sudo apt update && sudo apt upgrade -y
  sudo apt autoremove -y  # Removes unused dependencies

  echo "Stopping old containers..."
  sudo docker-compose down --remove-orphans

  echo "Removing unused containers, images, volumes and networks..." 
  sudo docker system prune -f

  echo "Pulling latest Docker images..."
  sudo docker-compose pull

  echo "Set correct permissions for .env file..."
  [ -f .env ] && sudo chmod 644 .env

  echo "Starting new containers..."
  sudo docker-compose up -d

  # rm -f .env
  echo "Deployment completed!"
EOF

# Step 5: Cleanup SSH Key and .env file
rm -f $TEMP_SSH_KEY
rm -f $ENV_FILE

echo "Successfully Deployed!"