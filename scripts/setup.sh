#!/bin/bash

set -e # Exit on Error

# Load variables from .env file
if [ -f .env ]; then
  source .env
else
  echo "Error: .env file not found!"
  exit 1
fi

# define file path and set variables
ENV_FILE=".env"

# copy all necessary files and scripts
echo "Copying files to EC2 instance..."
scp -i $AWS_SSH_KEY -o StrictHostKeyChecking=no $ENV_FILE $EC2_USER@$EC2_PUBLIC_IP:/home/ubuntu/
scp -i $AWS_SSH_KEY -o StrictHostKeyChecking=no $BACKUP_SCRIPT $EC2_USER@$EC2_PUBLIC_IP:/home/ubuntu/

# Connect to EC2 and update & upgrade the system
echo "Connecting to EC2 and updating system..."
ssh -i $AWS_SSH_KEY $EC2_USER@$EC2_PUBLIC_IP <<EOF
    # Update and upgrade the system
    sudo apt update && sudo apt upgrade -y
    
    # Install Docker
    echo "Installing Docker and Docker Compose..."
    sudo apt install -y docker.io
    
    # Start Docker and enable it to run on startup
    sudo systemctl start docker
    sudo systemctl enable docker

    # Install Docker Compose
    sudo curl -L "https://github.com/docker/compose/releases/download/$(curl -s https://api.github.com/repos/docker/compose/releases/latest | jq -r .tag_name)/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

    # Set permissions for Docker Compose
    sudo chmod +x /usr/local/bin/docker-compose

    # Verify Docker Compose installation
    docker-compose --version
    echo "Docker and Docker Compose installed successfully!"

    # Setting up cronjobs
    mkdir -p ~/logs

    # Automating Tasks with Cronjobs
    chmod +x ~/backup_rds_prod_to_s3.sh

    echo "Setting up Cronjobs..."
    (crontab -l 2>/dev/null; echo "0 22 * * 0 ~/backup_rds_prod_to_s3.sh") | crontab -

    # ... more cronjobs (onboarding, offboarding, etl-pipeline)

    echo "Cronjobs set up successfully!"
EOF

echo "EC2 setup completed!"