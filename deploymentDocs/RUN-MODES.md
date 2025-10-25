# How to Run Meditation Center Application

This application can be run in **3 different modes**. Choose based on what you need.

**Author:** Sathira Basnayake

---

## 🏠 Mode 1: Development (Recommended for Daily Work)

**Use when:** Writing code, debugging, testing features

### Setup:

```bash
cd MeditationCenter

# Start database only
docker-compose up -d postgres-db

# Run Spring Boot from IDE (IntelliJ, VS Code, Eclipse)
# OR from terminal:
./gradlew bootRun
```

### Access:
```
http://localhost:8080
```

### What's Running:
- ✅ PostgreSQL (Docker container)
- ✅ Spring Boot (your IDE/local Java)
- ❌ NGINX (not needed)

### Pros:
- ⚡ Fast - Hot reload with Spring DevTools
- 🐛 Easy debugging - Set breakpoints in IDE
- 🔧 Simple - No NGINX complexity

### When to Use:
- Daily development
- Writing new features
- Fixing bugs
- Learning Spring Boot

---

## 🧪 Mode 2: Local Production Testing (Recommended Before Deploying)

**Use when:** Testing NGINX, learning production setup, testing before EC2

### Setup:

```bash
cd MeditationCenter

# Create environment file (first time only)
cp .env.local.example .env.local

# Start full production stack
docker-compose -f docker-compose.local.yml up -d --build
```

### Access:
```
http://localhost
```

### What's Running:
- ✅ PostgreSQL (Docker container)
- ✅ Spring Boot (Docker container)
- ✅ NGINX (Docker container)

### Pros:
- 🎯 Realistic - Same setup as production (minus SSL)
- 📚 Educational - Learn NGINX, reverse proxy
- 🔒 Safe - Test before deploying to EC2
- 💰 Free - No cloud costs

### When to Use:
- Before deploying to EC2
- Testing NGINX configuration
- Learning production setup
- Practicing deployments

**Guide:** See `LOCAL-PRODUCTION-TEST.md`

---

## ☁️ Mode 3: Production (AWS EC2)

**Use when:** Deploying for real users, production environment

### Setup:

```bash
# On AWS EC2 instance
cd meditation-center/MeditationCenter

# Create environment file (first time only)
cp .env.production.example .env.production
nano .env.production  # Add strong passwords!

# Deploy
docker-compose -f docker-compose.prod.yml up -d --build
```

### Access:
```
https://yourdomain.com
```

### What's Running:
- ✅ PostgreSQL (Docker container)
- ✅ Spring Boot (Docker container)
- ✅ NGINX (Docker container)
- ✅ SSL Certificate (Let's Encrypt)

### Pros:
- 🌐 Public - Accessible from internet
- 🔐 Secure - HTTPS with SSL
- 💪 Production-ready - Real deployment

### When to Use:
- Real production deployment
- Serving actual users
- Public demos

**Guide:** See `DEPLOYMENT.md`

---

## Quick Comparison

| Aspect | Development | Local Production Test | Production (EC2) |
|--------|------------|----------------------|------------------|
| **Database** | Docker | Docker | Docker |
| **Spring Boot** | Local (IDE) | Docker | Docker |
| **NGINX** | ❌ No | ✅ Yes (HTTP) | ✅ Yes (HTTPS) |
| **SSL** | ❌ No | ❌ No | ✅ Yes |
| **Access** | localhost:8080 | localhost | yourdomain.com |
| **Hot Reload** | ✅ Yes | ❌ No | ❌ No |
| **Debugging** | ✅ Easy | ⚠️ Harder | ⚠️ Harder |
| **Cost** | Free | Free | $7-10/month |
| **Internet Access** | ❌ No | ❌ No | ✅ Yes |

---

## Typical Workflow

### Day-to-Day Development:

```bash
# Morning: Start database
docker-compose up -d postgres-db

# All day: Code in IDE, run from IDE
# (Hot reload, debugging, etc.)

# Evening: Stop database (optional)
docker-compose down
```

### Before Deploying to Production:

```bash
# Test full stack locally
docker-compose -f docker-compose.local.yml up -d --build

# Access at http://localhost
# Test everything works

# Stop
docker-compose -f docker-compose.local.yml down
```

### Deploying to Production:

```bash
# On EC2:
git pull
docker-compose -f docker-compose.prod.yml up -d --build

# Access at https://yourdomain.com
```

---

## Switching Between Modes

### From Development to Local Testing:

```bash
# Stop development
docker-compose down

# Start local testing
docker-compose -f docker-compose.local.yml up -d --build
```

### From Local Testing to Development:

```bash
# Stop local testing
docker-compose -f docker-compose.local.yml down

# Start development
docker-compose up -d postgres-db
./gradlew bootRun
```

---

## File Reference

| File | Purpose | Used By |
|------|---------|---------|
| `docker-compose.yml` | Development (DB only) | Mode 1 |
| `docker-compose.local.yml` | Local production testing | Mode 2 |
| `docker-compose.prod.yml` | Production deployment | Mode 3 |
| `.env.example` | Development secrets template | Mode 1 |
| `.env.local.example` | Local testing secrets template | Mode 2 |
| `.env.production.example` | Production secrets template | Mode 3 |
| `nginx/nginx.local.conf` | NGINX config (HTTP) | Mode 2 |
| `nginx/nginx.conf` | NGINX config (HTTPS) | Mode 3 |

---

## Documentation Reference

| Document | Purpose |
|----------|---------|
| **RUN-MODES.md** (this file) | Overview of all run modes |
| **LOCAL-PRODUCTION-TEST.md** | How to test production setup locally |
| **DEPLOYMENT.md** | Complete AWS EC2 deployment guide |
| **DEPLOYMENT-QUICKSTART.md** | Quick deployment reference |

---

## Common Questions

### Q: Which mode should I use most of the time?
**A:** Mode 1 (Development) - It's fastest and easiest for daily coding.

### Q: When should I use Mode 2 (Local Production Testing)?
**A:** Before deploying to EC2 for the first time, or when testing NGINX configuration changes.

### Q: Can I use Mode 2 for regular development?
**A:** You can, but it's slower (no hot reload) and harder to debug. Use Mode 1 for development.

### Q: Do I need to stop one mode before starting another?
**A:** Yes! Stop the current mode first to avoid port conflicts.

### Q: Which ports are used in each mode?

| Mode | Port 80 | Port 8080 | Port 5432 |
|------|---------|-----------|-----------|
| Development | - | Spring Boot | PostgreSQL |
| Local Testing | NGINX | (internal) | PostgreSQL |
| Production | NGINX (redirects to 443) | (internal) | (internal) |

---

## Next Steps

1. **Start with Mode 1** - Get familiar with development
2. **Try Mode 2** - Learn production setup safely
3. **Deploy Mode 3** - Go to production when ready

---

**Happy coding!** 🚀
