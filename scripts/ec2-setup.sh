##########################################################
# File: setup.sh                                         #
# Version: 1.0                                           #
# Author: Janis Grüttmüller on 11.03.2025                #
# Description: script to setup docker, docker-compose    #
# and copy all necessary files on the EC2 instanc        #
#                                                        #
# change history:                                        #
# 11.03.2025 - initial version                           #
##########################################################

#!/bin/bash
set -e # Exit on Error

# Load variables from .env file
if [ -f .env ]; then
  source .env
fi

# define file path and set variables
EC2_SSH_KEY="secrets/AWS_EC2_ACCESS_KEY.pem"

# Connect to EC2 and update & upgrade the system
echo "Connecting to EC2 and updating system..."
ssh -i $EC2_SSH_KEY $EC2_USER@$EC2_ELASTIC_IP <<EOF
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
    sudo docker-compose --version
    echo "Docker and Docker Compose installed successfully!"

    # Setup automated Docker cleanup cronjob
    echo "Setting up automated Docker cleanup cronjob (daily at 3:00 AM UTC)..."
    (crontab -l 2>/dev/null; echo "0 3 * * * sudo docker system prune --filter \"until=24h\" -f") | crontab -
    echo "Automated Docker cleanup cronjob set up."

    # uncomment in case other cronjobs are necessary
    # Create directory for cronjob error logs
    # mkdir -p ~/logs

    # Make scripts executable
    # chmod +x ~/my-script.sh

    # echo "Setting up other Cronjobs..."
    # (crontab -l 2>/dev/null; echo "0 22 * * 0 ~/my-script.sh") | crontab -

    # ... more cronjobs (onboarding, offboarding, etl-pipeline)

    # echo "Other Cronjobs set up successfully!"
EOF

echo "EC2 setup completed!"