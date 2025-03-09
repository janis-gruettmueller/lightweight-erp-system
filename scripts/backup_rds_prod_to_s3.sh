#############################################################
# File: backup_rds_prod_to_s3.sh                            #
# Version: 1.0                                              #
# Author: Janis Grüttmüller on 13.02.2025                   #
# Description: This script creates a current backup of      #
# the LeanX ERP System productive rds mySQL database        #
# instance (erp-prod) and stores it in an s3 bucket         #
#                                                           #
# change history:                                           #
# 13.02.2025 - initial version                              #
#############################################################

#!/bin/bash

# Database Credentials
DB_USER="root"
DB_PASSWORD=""
DB_NAME="leanx_erp_prod"
BACKUP_DIR="database/backups"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_FILE="${DB_NAME}_backup_$TIMESTAMP.sql"

# Create Backup Directory if not exists
mkdir -p "$BACKUP_DIR"

# Run mysqldump
echo "create backup..."
mysqldump -u "$DB_USER" -p "$DB_PASSWORD" "$DB_NAME" > "$BACKUP_FILE"

# Optional: Compress Backup
# gzip "$BACKUP_FILE"

echo "Backup saved as $BACKUP_FILE"