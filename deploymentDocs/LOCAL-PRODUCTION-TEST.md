# Testing Production Setup Locally

This guide shows you how to run the **full production stack** (NGINX + Spring Boot + PostgreSQL) on your local machine for testing.

**Author:** Sathira Basnayake

---

## Why Test Production Setup Locally?

✅ **Learn NGINX** - Understand how reverse proxy works
✅ **Test before deploying** - Catch issues before EC2
✅ **Practice the full stack** - See how all pieces connect
✅ **Debug safely** - No risk to production
✅ **Save money** - No EC2 costs while learning

---

## Quick Start

### Step 1: Create Environment File

```bash
cd MeditationCenter

# Copy local environment template
cp .env.local.example .env.local

# You can use the default values for local testing
# (They're already safe for local use)
```

### Step 2: Run Full Production Stack

```bash
# Build and start everything
docker-compose -f docker-compose.local.yml up -d --build

# Watch logs
docker-compose -f docker-compose.local.yml logs -f

# Wait for: "Started MeditationCenterApplication"
# Press Ctrl+C to exit logs
```

### Step 3: Access Your Application

Open browser:
```
http://localhost
```

**You're now accessing your app through NGINX!** 🎉

---

## What's Running?

```bash
# Check status
docker-compose -f docker-compose.local.yml ps
```

You should see:

| Container | Port | Purpose |
|-----------|------|---------|
| **meditation-nginx-local** | 80 | NGINX reverse proxy |
| **meditation-app-local** | (internal) | Spring Boot app |
| **meditation-postgres-local** | 5432 | PostgreSQL database |

---

## How It Works

### Request Flow:

```
Your Browser (http://localhost)
    ↓
NGINX (port 80)
    ↓ [reverse proxy]
Spring Boot (port 8080 - internal only)
    ↓
PostgreSQL (port 5432)
```

**Key Points:**
- You access via **NGINX** on port 80 (not Spring Boot directly)
- Spring Boot port 8080 is **NOT exposed** to your machine (only to NGINX)
- This is **exactly** how it works in production (except production uses HTTPS)

---

## Differences: Local vs Production

| Aspect | Local Testing | Production (EC2) |
|--------|--------------|------------------|
| **Protocol** | HTTP only | HTTPS (SSL) |
| **URL** | http://localhost | https://yourdomain.com |
| **SSL Certificate** | None | Let's Encrypt |
| **Passwords** | Simple (local_password) | Strong (generated) |
| **Database Port** | Exposed (5432) | Hidden |
| **Spring Profile** | dev | prod |
| **Access** | Your machine only | Internet |

---

## Testing Scenarios

### Test 1: NGINX is Working

```bash
# From your local machine
curl http://localhost

# Should return HTML or JSON from your Spring Boot app
```

### Test 2: Rate Limiting

```bash
# Test rate limiting (10 req/second limit)
for i in {1..15}; do curl http://localhost; done

# After 10 requests, you should get: 429 Too Many Requests
```

### Test 3: Health Check

```bash
# Health check endpoint (no rate limiting)
curl http://localhost/actuator/health

# Should return: {"status":"UP"}
```

### Test 4: Proxy Headers

```bash
# Check if NGINX is adding proxy headers
curl -v http://localhost

# Look for headers like:
# X-Real-IP
# X-Forwarded-For
# X-Frame-Options (security)
# X-Content-Type-Options (security)
```

### Test 5: Static File Caching

```bash
# If you serve static files, check caching
curl -I http://localhost/static/image.png

# Should show:
# Cache-Control: public, immutable
# Expires: (30 days from now)
```

---

## Comparing with Regular Development

### Regular Development (What You've Been Doing):

```bash
# Start database only
docker-compose up -d postgres-db

# Run Spring Boot from IDE
./gradlew bootRun

# Access directly
http://localhost:8080
```

**Flow:**
```
Browser → Spring Boot (8080) → PostgreSQL
```

### Production Testing (New):

```bash
# Start full stack
docker-compose -f docker-compose.local.yml up -d --build

# Access through NGINX
http://localhost
```

**Flow:**
```
Browser → NGINX (80) → Spring Boot (8080) → PostgreSQL
```

---

## Viewing NGINX Logs

### Access Logs (All Requests):

```bash
# View NGINX access logs
cat nginx/logs/access.log

# Or tail in real-time
tail -f nginx/logs/access.log
```

**Example log entry:**
```
192.168.1.100 - - [25/Oct/2025:14:30:15 +0000] "GET / HTTP/1.1" 200 1234
```

### Error Logs:

```bash
# View NGINX error logs
cat nginx/logs/error.log
```

### Application Logs:

```bash
# Spring Boot logs
docker-compose -f docker-compose.local.yml logs -f app
```

---

## Modifying NGINX Configuration

### Test Configuration Changes:

1. **Edit NGINX config:**
```bash
nano nginx/nginx.local.conf
```

2. **Test configuration:**
```bash
# Check if config is valid
docker exec meditation-nginx-local nginx -t

# Should show: syntax is ok, test is successful
```

3. **Reload NGINX:**
```bash
# Reload without downtime
docker exec meditation-nginx-local nginx -s reload

# Or restart container
docker-compose -f docker-compose.local.yml restart nginx
```

### Example: Change Rate Limit

```nginx
# In nginx/nginx.local.conf
# Change from 10 req/sec to 5 req/sec
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=5r/s;
```

```bash
# Test and reload
docker exec meditation-nginx-local nginx -t
docker exec meditation-nginx-local nginx -s reload

# Test new limit
for i in {1..10}; do curl http://localhost; done
# Should hit 429 after 5 requests now
```

---

## Troubleshooting

### Issue: Can't Access http://localhost

**Check if NGINX is running:**
```bash
docker-compose -f docker-compose.local.yml ps nginx
# Should show: Up
```

**Check NGINX logs:**
```bash
docker-compose -f docker-compose.local.yml logs nginx
```

**Check if port 80 is already in use:**
```bash
lsof -i :80

# If something else is using port 80, stop it or change port in docker-compose.local.yml
# Change: "80:80" to "8081:80"
# Then access at: http://localhost:8081
```

### Issue: 502 Bad Gateway

**Means:** NGINX can't connect to Spring Boot

**Check if app is running:**
```bash
docker-compose -f docker-compose.local.yml ps app
# Should show: Up (healthy)
```

**Check app logs:**
```bash
docker-compose -f docker-compose.local.yml logs app | tail -50
```

**Restart app:**
```bash
docker-compose -f docker-compose.local.yml restart app
```

### Issue: Changes Not Showing

**Rebuild containers:**
```bash
# Stop everything
docker-compose -f docker-compose.local.yml down

# Rebuild and start
docker-compose -f docker-compose.local.yml up -d --build
```

### Issue: Database Connection Error

**Check database:**
```bash
docker-compose -f docker-compose.local.yml ps postgres-db
# Should show: Up (healthy)
```

**Check database logs:**
```bash
docker-compose -f docker-compose.local.yml logs postgres-db
```

**Connect to database manually:**
```bash
docker exec -it meditation-postgres-local psql -U local_user -d meditation_db
```

---

## Cleaning Up

### Stop Everything:

```bash
docker-compose -f docker-compose.local.yml down
```

### Stop and Remove Volumes (Fresh Start):

```bash
# WARNING: This deletes all database data!
docker-compose -f docker-compose.local.yml down -v
```

### Keep Database, Restart Containers:

```bash
# Stop
docker-compose -f docker-compose.local.yml down

# Start fresh
docker-compose -f docker-compose.local.yml up -d --build
```

---

## Learning Exercises

### Exercise 1: Test NGINX Security Headers

```bash
# Check what headers NGINX adds
curl -I http://localhost

# Look for:
# X-Frame-Options: SAMEORIGIN
# X-Content-Type-Options: nosniff
# X-XSS-Protection: 1; mode=block
```

### Exercise 2: Test Rate Limiting

```bash
# Create a test script
cat > test-rate-limit.sh << 'EOF'
#!/bin/bash
for i in {1..20}; do
  echo "Request $i:"
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost
  sleep 0.05
done
EOF

chmod +x test-rate-limit.sh
./test-rate-limit.sh

# You should see some 429 responses (rate limited)
```

### Exercise 3: Monitor Real-Time Logs

```bash
# Open 3 terminal windows

# Terminal 1: NGINX access logs
tail -f nginx/logs/access.log

# Terminal 2: Application logs
docker-compose -f docker-compose.local.yml logs -f app

# Terminal 3: Make requests
while true; do curl http://localhost; sleep 1; done

# Watch logs update in real-time!
```

### Exercise 4: Simulate Production Deployment

```bash
# Make a code change
# Edit any Java file in src/

# "Deploy" the change
docker-compose -f docker-compose.local.yml up -d --build

# Watch deployment
docker-compose -f docker-compose.local.yml logs -f app

# Verify change is live
curl http://localhost
```

---

## Switching Between Setups

### Development (No NGINX):

```bash
# Stop production stack if running
docker-compose -f docker-compose.local.yml down

# Start just database
docker-compose up -d postgres-db

# Run from IDE
./gradlew bootRun

# Access at: http://localhost:8080
```

### Local Production Testing (With NGINX):

```bash
# Stop development if running
docker-compose down

# Start full stack
docker-compose -f docker-compose.local.yml up -d --build

# Access at: http://localhost
```

---

## Next Steps

After testing locally:

1. ✅ **Understand NGINX** - You now know how reverse proxy works
2. ✅ **Test configurations** - Try changing NGINX settings
3. ✅ **Practice deployments** - Simulate code changes and "deployments"
4. ✅ **Ready for EC2** - You know exactly what will run on production

**When ready for real production:**
→ Follow **DEPLOYMENT.md** to deploy to AWS EC2

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `docker-compose -f docker-compose.local.yml up -d` | Start full stack |
| `docker-compose -f docker-compose.local.yml down` | Stop everything |
| `docker-compose -f docker-compose.local.yml logs -f` | View all logs |
| `docker-compose -f docker-compose.local.yml ps` | Check status |
| `docker-compose -f docker-compose.local.yml restart nginx` | Restart NGINX |
| `docker exec meditation-nginx-local nginx -t` | Test NGINX config |
| `docker exec meditation-nginx-local nginx -s reload` | Reload NGINX |
| `curl http://localhost` | Test application |
| `curl http://localhost/actuator/health` | Test health check |

---

## Summary

**You can now:**
✅ Run the full production stack locally
✅ Test NGINX configuration safely
✅ Practice deployments
✅ Debug issues before EC2
✅ Learn how production works

**Access:**
- **Development:** http://localhost:8080 (direct)
- **Production Testing:** http://localhost (via NGINX)
- **Real Production:** https://yourdomain.com (via NGINX + SSL)

---

**Happy testing!** 🚀
