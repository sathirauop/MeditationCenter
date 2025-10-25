# AWS EC2 Deployment Guide - Meditation Center
## Production Deployment with Docker + NGINX + Let's Encrypt SSL

**Author:** Sathira Basnayake
**Last Updated:** 2025-10-25

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [AWS EC2 Setup](#aws-ec2-setup)
3. [Server Initial Configuration](#server-initial-configuration)
4. [Install Required Software](#install-required-software)
5. [Domain and DNS Setup](#domain-and-dns-setup)
6. [Deploy Application](#deploy-application)
7. [SSL Certificate Setup (Let's Encrypt)](#ssl-certificate-setup)
8. [Verify Deployment](#verify-deployment)
9. [Maintenance Tasks](#maintenance-tasks)
10. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### What You Need

- ✅ AWS Account (Free tier eligible)
- ✅ Domain name (from Namecheap, GoDaddy, etc.)
- ✅ SSH client (Terminal on Mac/Linux, PuTTY on Windows)
- ✅ Git repository with your code
- ✅ Basic command line knowledge

### Estimated Costs

| Service | Monthly Cost |
|---------|-------------|
| EC2 t3.micro (1 vCPU, 1GB RAM) | ~$7-10 |
| Elastic IP | $0 (if attached to running instance) |
| **Total** | **~$7-10/month** |

---

## AWS EC2 Setup

### Step 1: Launch EC2 Instance

1. **Log in to AWS Console**
   - Go to https://console.aws.amazon.com
   - Navigate to EC2 Dashboard

2. **Launch Instance**
   - Click "Launch Instance"

3. **Choose Configuration:**

   **Name:** `meditation-center-prod`

   **AMI:** Ubuntu Server 22.04 LTS (Free tier eligible)

   **Instance Type:** t3.micro (1 vCPU, 1 GB RAM)
   - Free tier: 750 hours/month

   **Key Pair:**
   - Create new key pair: `meditation-center-key`
   - Type: RSA
   - Format: .pem (Mac/Linux) or .ppk (Windows)
   - **Download and save securely** (you can't download again!)

   **Network Settings:**
   - Create security group: `meditation-center-sg`
   - Allow SSH (22) from your IP
   - Allow HTTP (80) from anywhere
   - Allow HTTPS (443) from anywhere

   **Storage:**
   - 20 GB gp3 (General Purpose SSD)

4. **Launch Instance**
   - Click "Launch Instance"
   - Wait for instance to be "Running"

### Step 2: Allocate Elastic IP

**Why?** So your IP address doesn't change when you restart the instance.

```bash
# In AWS Console:
# EC2 → Elastic IPs → Allocate Elastic IP address
# Then: Actions → Associate Elastic IP address
# Select your meditation-center-prod instance
```

**Your Elastic IP:** `3.xxx.xxx.xxx` (write this down!)

---

## Server Initial Configuration

### Step 1: Connect to Your Server

```bash
# Set correct permissions for key file
chmod 400 ~/Downloads/meditation-center-key.pem

# Connect to EC2 instance
# Replace 3.xxx.xxx.xxx with your Elastic IP
ssh -i ~/Downloads/meditation-center-key.pem ubuntu@3.xxx.xxx.xxx
```

You should see:
```
Welcome to Ubuntu 22.04.3 LTS
ubuntu@ip-xxx-xxx-xxx-xxx:~$
```

### Step 2: Update System

```bash
# Update package list
sudo apt update

# Upgrade all packages
sudo apt upgrade -y

# Reboot (optional but recommended)
sudo reboot

# Wait 30 seconds, then reconnect
ssh -i ~/Downloads/meditation-center-key.pem ubuntu@3.xxx.xxx.xxx
```

### Step 3: Create Swap File (Prevent Out of Memory)

t3.micro only has 1GB RAM. Add swap space:

```bash
# Create 2GB swap file
sudo fallocate -l 2G /swapfile

# Set permissions
sudo chmod 600 /swapfile

# Make swap
sudo mkswap /swapfile

# Enable swap
sudo swapon /swapfile

# Make permanent
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# Verify
free -h
# Should show 2GB swap
```

---

## Install Required Software

### Step 1: Install Docker

```bash
# Install prerequisites
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Add Docker GPG key
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Update package list
sudo apt update

# Install Docker
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Start Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add ubuntu user to docker group (no need for sudo)
sudo usermod -aG docker ubuntu

# Log out and back in for group changes to take effect
exit

# Reconnect
ssh -i ~/Downloads/meditation-center-key.pem ubuntu@3.xxx.xxx.xxx

# Verify Docker works
docker --version
# Should show: Docker version 24.x.x
```

### Step 2: Install Docker Compose

```bash
# Download Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.23.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# Make executable
sudo chmod +x /usr/local/bin/docker-compose

# Verify
docker-compose --version
# Should show: Docker Compose version v2.23.0
```

### Step 3: Install Git

```bash
# Install Git
sudo apt install -y git

# Verify
git --version
# Should show: git version 2.x.x
```

### Step 4: Install Certbot (for Let's Encrypt SSL)

```bash
# Install Certbot
sudo apt install -y certbot

# Verify
certbot --version
# Should show: certbot 1.x.x
```

---

## Domain and DNS Setup

### Step 1: Point Domain to EC2 IP

**In your domain registrar** (Namecheap, GoDaddy, etc.):

1. **Add A Record:**
   ```
   Type: A
   Host: @
   Value: 3.xxx.xxx.xxx (your Elastic IP)
   TTL: 3600
   ```

2. **Add A Record for www:**
   ```
   Type: A
   Host: www
   Value: 3.xxx.xxx.xxx (your Elastic IP)
   TTL: 3600
   ```

### Step 2: Verify DNS Propagation

```bash
# Wait 5-10 minutes, then test
# Replace meditation.example.com with your domain

# From your local machine:
ping meditation.example.com
# Should respond with your Elastic IP

# Check DNS
nslookup meditation.example.com
# Should show your Elastic IP
```

---

## Deploy Application

### Step 1: Clone Repository

```bash
# On EC2 server

# Generate SSH key for GitHub
ssh-keygen -t ed25519 -C "your-email@example.com"
# Press Enter 3 times (default location, no passphrase)

# Display public key
cat ~/.ssh/id_ed25519.pub

# Copy the output and add to GitHub:
# GitHub → Settings → SSH and GPG keys → New SSH key
```

```bash
# Clone your repository
cd ~
git clone git@github.com:your-username/meditation-center.git
# Or use HTTPS:
# git clone https://github.com/your-username/meditation-center.git

# Navigate to project
cd meditation-center/MeditationCenter
```

### Step 2: Configure Environment Variables

```bash
# Copy production environment template
cp .env.production.example .env.production

# Edit environment file
nano .env.production
```

**Fill in these CRITICAL values:**

```bash
# Generate strong database password
DB_PASSWORD=$(openssl rand -base64 32)

# Generate strong JWT secret (64+ characters)
JWT_SECRET=$(openssl rand -base64 64)

# Update domain
JWT_ISSUER=meditation.example.com
APP_URL=https://meditation.example.com
CORS_ALLOWED_ORIGINS=https://meditation.example.com,https://www.meditation.example.com
```

**Save:** Ctrl+X, then Y, then Enter

### Step 3: Update NGINX Configuration with Your Domain

```bash
# Edit NGINX config
nano nginx/nginx.conf
```

**Replace ALL instances of `meditation.example.com` with YOUR domain.**

**Find and replace (3 places):**
```nginx
server_name meditation.example.com www.meditation.example.com;
```

**Save:** Ctrl+X, then Y, then Enter

### Step 4: Initial Deployment (HTTP Only - Before SSL)

First, we deploy without SSL to get Let's Encrypt certificate:

```bash
# Temporarily comment out SSL lines in nginx.conf
nano nginx/nginx.conf

# Comment out these lines in the HTTPS server block:
# ssl_certificate /etc/letsencrypt/...
# ssl_certificate_key /etc/letsencrypt/...
# ssl_trusted_certificate /etc/letsencrypt/...

# Also comment out the entire HTTPS server block for now
# We'll uncomment after getting SSL certificate
```

```bash
# Start only HTTP redirect and app
docker-compose -f docker-compose.prod.yml up -d postgres-db app

# Check if app is running
docker-compose -f docker-compose.prod.yml ps

# Check logs
docker-compose -f docker-compose.prod.yml logs -f app
# Wait for: "Started MeditationCenterApplication"
# Press Ctrl+C to exit logs
```

---

## SSL Certificate Setup

### Step 1: Obtain Let's Encrypt Certificate

```bash
# Stop NGINX if running
docker-compose -f docker-compose.prod.yml stop nginx

# Request certificate
# Replace meditation.example.com with YOUR domain
sudo certbot certonly --standalone \
  -d meditation.example.com \
  -d www.meditation.example.com \
  --email your-email@example.com \
  --agree-tos \
  --no-eff-email

# You should see: Successfully received certificate
```

**Certificate locations:**
```
Certificate: /etc/letsencrypt/live/meditation.example.com/fullchain.pem
Private Key: /etc/letsencrypt/live/meditation.example.com/privkey.pem
Chain: /etc/letsencrypt/live/meditation.example.com/chain.pem
```

### Step 2: Set Up Auto-Renewal

Let's Encrypt certificates expire after 90 days. Set up auto-renewal:

```bash
# Test renewal
sudo certbot renew --dry-run

# Should show: Congratulations, all simulated renewals succeeded

# Add cron job for auto-renewal
sudo crontab -e
# Choose nano (option 1)

# Add this line at the end:
0 3 * * * certbot renew --quiet --post-hook "docker-compose -f /home/ubuntu/meditation-center/MeditationCenter/docker-compose.prod.yml restart nginx"

# Save: Ctrl+X, then Y, then Enter
```

This runs every day at 3 AM and renews if needed.

### Step 3: Enable NGINX with SSL

```bash
# Uncomment SSL lines in nginx.conf
nano nginx/nginx.conf

# Uncomment these lines:
# ssl_certificate /etc/letsencrypt/...
# ssl_certificate_key /etc/letsencrypt/...
# ssl_trusted_certificate /etc/letsencrypt/...

# Uncomment the entire HTTPS server block

# Save: Ctrl+X, then Y, then Enter
```

### Step 4: Start Full Production Stack

```bash
# Start everything
docker-compose -f docker-compose.prod.yml up -d --build

# Check status
docker-compose -f docker-compose.prod.yml ps

# Should show:
# meditation-nginx      Up
# meditation-app        Up (healthy)
# meditation-postgres   Up (healthy)

# Check logs
docker-compose -f docker-compose.prod.yml logs -f

# Press Ctrl+C to exit
```

---

## Verify Deployment

### Step 1: Test HTTPS

```bash
# From your local machine
curl https://meditation.example.com

# Should return HTML or JSON response
```

### Step 2: Test in Browser

Open browser:
```
https://meditation.example.com
```

**You should see:**
- ✅ Padlock icon (HTTPS enabled)
- ✅ Your application running
- ✅ No certificate warnings

### Step 3: Test SSL Rating

Go to: https://www.ssllabs.com/ssltest/

Enter your domain and get security rating (should be A or A+)

### Step 4: Check Docker Container Health

```bash
# On EC2 server
docker-compose -f docker-compose.prod.yml ps

# All containers should show "Up" and "healthy"
```

---

## Maintenance Tasks

### Daily Tasks

**Check Application Logs:**
```bash
docker-compose -f docker-compose.prod.yml logs --tail=100 app
```

### Weekly Tasks

**1. Backup Database:**
```bash
# Create backup directory
mkdir -p ~/backups

# Backup database
docker exec meditation-postgres pg_dump -U meditation_prod_user meditation_db > ~/backups/backup-$(date +%Y%m%d).sql

# Verify backup
ls -lh ~/backups/
```

**2. Check Disk Space:**
```bash
df -h

# Should have at least 20% free
```

**3. Review Logs:**
```bash
# Check for errors
docker-compose -f docker-compose.prod.yml logs --tail=500 | grep ERROR
```

### Monthly Tasks

**1. Update System:**
```bash
sudo apt update && sudo apt upgrade -y
```

**2. Update Docker Images:**
```bash
# Pull latest images
docker-compose -f docker-compose.prod.yml pull

# Recreate containers
docker-compose -f docker-compose.prod.yml up -d --build
```

**3. Clean Up Docker:**
```bash
# Remove unused images
docker image prune -a -f

# Remove unused volumes
docker volume prune -f
```

**4. Rotate Backups:**
```bash
# Keep only last 30 days
find ~/backups/ -name "backup-*.sql" -mtime +30 -delete
```

### Automated Backup Script

```bash
# Create backup script
nano ~/backup-db.sh
```

**Add this content:**
```bash
#!/bin/bash
# Database backup script

BACKUP_DIR="/home/ubuntu/backups"
DATE=$(date +%Y%m%d_%H%M%S)
CONTAINER="meditation-postgres"
DB_USER="meditation_prod_user"
DB_NAME="meditation_db"

# Create backup
docker exec $CONTAINER pg_dump -U $DB_USER $DB_NAME > $BACKUP_DIR/backup-$DATE.sql

# Compress
gzip $BACKUP_DIR/backup-$DATE.sql

# Delete backups older than 30 days
find $BACKUP_DIR -name "backup-*.sql.gz" -mtime +30 -delete

echo "Backup completed: backup-$DATE.sql.gz"
```

```bash
# Make executable
chmod +x ~/backup-db.sh

# Test
~/backup-db.sh

# Add to crontab (daily at 2 AM)
crontab -e

# Add line:
0 2 * * * /home/ubuntu/backup-db.sh >> /home/ubuntu/backup.log 2>&1
```

---

## Troubleshooting

### Issue: Can't connect to HTTPS

**Check:**
1. DNS is pointing to correct IP:
   ```bash
   nslookup meditation.example.com
   ```

2. Firewall allows HTTPS (port 443):
   ```bash
   sudo ufw status
   # Should show: 443/tcp ALLOW
   ```

3. NGINX is running:
   ```bash
   docker-compose -f docker-compose.prod.yml ps nginx
   ```

4. SSL certificate exists:
   ```bash
   sudo ls -la /etc/letsencrypt/live/meditation.example.com/
   ```

### Issue: 502 Bad Gateway

**Means:** NGINX can't connect to Spring Boot app

**Fix:**
```bash
# Check if app is running
docker-compose -f docker-compose.prod.yml ps app

# Check app logs
docker-compose -f docker-compose.prod.yml logs app

# Restart app
docker-compose -f docker-compose.prod.yml restart app
```

### Issue: Application crashes / Out of Memory

**Fix:**
```bash
# Check memory usage
free -h

# Check Docker stats
docker stats

# If using too much memory, reduce Java heap:
# Edit docker-compose.prod.yml
# Change: JAVA_OPTS=-Xms256m -Xmx512m
```

### Issue: Database won't start

**Check:**
```bash
# Database logs
docker-compose -f docker-compose.prod.yml logs postgres-db

# Check disk space
df -h

# If disk full, clean Docker:
docker system prune -a -f
```

### Issue: SSL Certificate Renewal Fails

**Fix:**
```bash
# Stop NGINX
docker-compose -f docker-compose.prod.yml stop nginx

# Renew manually
sudo certbot renew

# Restart NGINX
docker-compose -f docker-compose.prod.yml start nginx
```

### Emergency Rollback

**If deployment breaks:**
```bash
# Stop everything
docker-compose -f docker-compose.prod.yml down

# Restore from backup
docker exec -i meditation-postgres psql -U meditation_prod_user meditation_db < ~/backups/backup-20250101.sql

# Checkout previous working commit
git log --oneline
git checkout <previous-commit-hash>

# Rebuild and restart
docker-compose -f docker-compose.prod.yml up -d --build
```

---

## Next Steps

After successful deployment:

1. ✅ Set up monitoring (Uptime Robot, Pingdom)
2. ✅ Configure backup retention policy
3. ✅ Set up error tracking (Sentry)
4. ✅ Create staging environment
5. ✅ Document API endpoints
6. ✅ Set up CI/CD pipeline

---

## Support

**Issues?** Check logs first:
```bash
docker-compose -f docker-compose.prod.yml logs -f
```

**Need help?** Contact: admin@meditation.example.com

---

**Congratulations! Your application is now running in production!** 🎉
