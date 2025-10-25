# Quick Deployment Reference

**For detailed instructions, see DEPLOYMENT.md**

## One-Time Setup on EC2

```bash
# 1. Connect to EC2
ssh -i meditation-center-key.pem ubuntu@YOUR_IP

# 2. Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu
# Log out and back in

# 3. Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.23.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 4. Install Certbot
sudo apt install -y certbot

# 5. Clone repo
git clone YOUR_REPO_URL
cd meditation-center/MeditationCenter

# 6. Configure environment
cp .env.production.example .env.production
nano .env.production
# Fill in: DB_PASSWORD, JWT_SECRET, domain

# 7. Update NGINX config with your domain
nano nginx/nginx.conf
# Replace meditation.example.com with YOUR domain

# 8. Get SSL certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# 9. Deploy
docker-compose -f docker-compose.prod.yml up -d --build
```

## Daily Operations

```bash
# View logs
docker-compose -f docker-compose.prod.yml logs -f

# Restart application
docker-compose -f docker-compose.prod.yml restart app

# Stop everything
docker-compose -f docker-compose.prod.yml down

# Start everything
docker-compose -f docker-compose.prod.yml up -d
```

## Deployment After Code Changes

```bash
# On EC2:
cd ~/meditation-center/MeditationCenter

# Pull latest code
git pull

# Rebuild and restart
docker-compose -f docker-compose.prod.yml up -d --build

# Watch logs for errors
docker-compose -f docker-compose.prod.yml logs -f app
```

## Database Backup

```bash
# Manual backup
docker exec meditation-postgres pg_dump -U meditation_prod_user meditation_db > backup-$(date +%Y%m%d).sql

# Restore from backup
docker exec -i meditation-postgres psql -U meditation_prod_user meditation_db < backup-20250101.sql
```

## Check Status

```bash
# Container status
docker-compose -f docker-compose.prod.yml ps

# Health checks
curl https://yourdomain.com/actuator/health

# SSL rating
# Visit: https://www.ssllabs.com/ssltest/
```

## Troubleshooting

```bash
# Check logs
docker-compose -f docker-compose.prod.yml logs app | tail -100

# Check NGINX config
docker exec meditation-nginx nginx -t

# Restart NGINX
docker-compose -f docker-compose.prod.yml restart nginx

# Check disk space
df -h

# Check memory
free -h

# Clean Docker
docker system prune -a -f
```

## Security Checklist

- [ ] Changed DB_PASSWORD from default
- [ ] Changed JWT_SECRET to 64+ character string
- [ ] Updated domain in nginx.conf (3 places)
- [ ] SSL certificate installed
- [ ] .env.production not in Git
- [ ] Firewall configured (ports 22, 80, 443)
- [ ] Backup script configured
- [ ] Monitoring setup

## Important Files

| File | Purpose |
|------|---------|
| `docker-compose.prod.yml` | Production Docker configuration |
| `.env.production` | Production secrets (NOT in Git) |
| `nginx/nginx.conf` | NGINX reverse proxy config |
| `DEPLOYMENT.md` | Full deployment guide |

## Cost Estimate

| Item | Cost/Month |
|------|------------|
| EC2 t3.micro | $7-10 |
| Elastic IP | $0 (if attached) |
| **Total** | **$7-10** |

## Support

**Full Guide:** See DEPLOYMENT.md
**Issues:** Check logs first with `docker-compose logs`
