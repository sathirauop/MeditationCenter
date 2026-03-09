# Security Vulnerability Assessment Report

**Date**: 2026-01-10
**Application**: Meditation Center Spring Boot Application
**Security Rating**: 6.5/10 - **MEDIUM RISK**

---

## EXECUTIVE SUMMARY

Comprehensive security audit identified **23 security issues** across authentication, authorization, input validation, API security, and configuration. The application has **strong security foundations** (BCrypt password hashing, JWT authentication, jOOQ SQL injection protection, role-based access control) but **5 CRITICAL and 8 HIGH severity vulnerabilities** require immediate remediation.

**Most Critical Issues**:
1. ⚠️ **Weak JWT secret** - Can lead to complete authentication bypass
2. ⚠️ **Default admin password** (admin123) - Full system compromise
3. ⚠️ **No rate limiting** - Vulnerable to brute force attacks
4. ⚠️ **Exposed R2 credentials** - Storage compromise risk
5. ⚠️ **Development endpoint in production** - Information disclosure

**Recommendation**: **DO NOT deploy to production** until CRITICAL issues are resolved.

---

## CRITICAL VULNERABILITIES (5)

### 1. Weak JWT Secret - AUTHENTICATION BYPASS
**Severity**: 🔴 CRITICAL
**File**: `/MeditationCenter/.env:14-15`

**Issue**:
```bash
JWT_SECRET=change-this-to-a-secure-256-bit-secret-key-in-production-environment
```

**Impact**:
- Attacker can forge JWT tokens and impersonate any user (including admins)
- Complete authentication bypass
- Access to all admin endpoints without credentials

**Remediation**:
```bash
# Generate cryptographically secure secret (64+ characters)
openssl rand -base64 64 > jwt_secret.txt

# Update .env
JWT_SECRET=<generated-secret>
```

---

### 2. Default Admin Credentials - SYSTEM COMPROMISE
**Severity**: 🔴 CRITICAL
**File**: `/MeditationCenter/src/main/resources/db/migration/V14__seed_admin_user.sql:15`

**Issue**:
```sql
-- Password: admin123 (documented in migration file)
INSERT INTO users (email, password, ...) VALUES (
    'admin@meditationcenter.com',
    '$2a$10$2xwiXBeaYO2TjTO6BvTwluGENiBcK7PkZgyLZGmbOBh9.WqfhE5eG',
    ...
)
```

**Impact**:
- Publicly known credentials provide full administrative access
- Can create/modify/delete all data
- Can access all user information, payments, bookings

**Remediation**:
```java
// Option 1: Force password change on first login
if (user.isDefaultPassword()) {
    throw new AuthenticationException("Default password must be changed");
}

// Option 2: Remove seeded admin, require manual creation
// Delete V14__seed_admin_user.sql
```

---

### 3. No Rate Limiting - BRUTE FORCE ATTACKS
**Severity**: 🔴 CRITICAL
**Files**: All authentication endpoints

**Issue**: No rate limiting implementation found. Attackers can make unlimited requests to:
- `/api/auth/login` - Brute force passwords
- `/api/auth/register` - Account enumeration
- `/api/auth/refresh` - Token exhaustion

**Impact**:
- Brute force attacks on weak passwords (100+ attempts per second)
- Credential stuffing from leaked databases
- Denial of service via resource exhaustion

**Remediation**:
```java
// Add Bucket4j dependency and implement rate limiting
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter authenticationRateLimiter() {
        return RateLimiter.of("authentication", RateLimiterConfig.custom()
            .limitForPeriod(5)  // Max 5 attempts
            .limitRefreshPeriod(Duration.ofMinutes(15))
            .build());
    }
}
```

**Recommended Limits**:
- `/api/auth/login`: 5 requests per 15 minutes per IP
- `/api/auth/register`: 3 requests per hour per IP
- `/api/auth/refresh`: 10 requests per hour per token

---

### 4. Exposed R2 Credentials - DATA EXFILTRATION
**Severity**: 🔴 CRITICAL
**File**: `/MeditationCenter/.env:26-35`

**Issue**:
```bash
R2_ACCESS_KEY_ID=53dfd784434f7913e739e8a1b5a9035c
R2_SECRET_ACCESS_KEY=7730e84da4ff3aa1b54bb4ebe3b0db24cc105f91b05e512fe9378803e6678d98
```

**Impact**:
- Full access to all stored files (images, PDFs, documents)
- Can read/modify/delete R2 bucket contents
- Potential data exfiltration of user-uploaded content

**Remediation**:
1. **IMMEDIATELY** rotate R2 credentials in Cloudflare dashboard
2. Verify `.env` was never committed to git: `git log --all --full-history -- ".env"`
3. Use cloud-native secrets management (AWS Secrets Manager, HashiCorp Vault)

---

### 5. Development Endpoint in Production - INFORMATION DISCLOSURE
**Severity**: 🔴 CRITICAL
**File**: `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/util/HashGeneratorController.java`

**Issue**:
```java
@GetMapping(EndPoints.Util.HASH)
public String generateHash(@RequestParam String password) {
    return passwordEncoder.encode(password);  // PUBLIC ENDPOINT!
}
```

**SecurityConfig**:
```java
.requestMatchers(EndPoints.Util.FULL_PATH).permitAll()  // ⚠️ Exposed to internet
```

**Impact**:
- Attacker can generate BCrypt hashes
- CPU exhaustion (BCrypt is intentionally slow)
- Potential timing attacks to brute force hash collisions

**Remediation**:
```java
@Profile("dev")  // Only enable in development
@RestController
public class HashGeneratorController { ... }
```

---

## HIGH SEVERITY VULNERABILITIES (8)

### 6. No Token Blacklist - CANNOT REVOKE COMPROMISED TOKENS
**Severity**: 🟠 HIGH
**File**: `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/auth/logout/PostLogoutUseCase.java`

**Issue**:
```java
public void execute() {
    // TODO: Implement token blacklist in future
    // For now, client handles logout by deleting tokens
}
```

**Impact**:
- Logout is client-side only - tokens remain valid for 15 minutes
- Stolen tokens cannot be revoked
- User logs out but attacker continues access

**Remediation**: Implement token blacklist with Redis or database table.

---

### 7. No Refresh Token Rotation - LONG-TERM ACCESS IF STOLEN
**Severity**: 🟠 HIGH
**File**: `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/auth/refresh/PostRefreshUseCase.java`

**Issue**: Same refresh token can be reused multiple times for 7 days.

**Impact**: If refresh token is stolen, attacker has 7 days of access.

**Remediation**: Issue new refresh token with each refresh, invalidate old token.

---

### 8. XSS Vulnerabilities - STORED XSS IN BLOG/EVENTS
**Severity**: 🟠 HIGH
**Files**: Blog posts, event descriptions, activity descriptions

**Issue**: User-generated content returned without sanitization.

**Attack Example**:
```json
POST /api/admin/blog/post
{
  "title": "<script>alert('XSS')</script>",
  "content": "<img src=x onerror='steal_cookies()'>"
}
```

**Impact**:
- Session hijacking via JavaScript
- Admin panel defacement
- Credential theft

**Remediation**:
```java
@Service
public class TextSanitizer {
    public String sanitizeHtml(String html) {
        return Jsoup.clean(html, Whitelist.relaxed());
    }
}
```

---

### 9. Multipart Validation Bypass - INPUT VALIDATION FAILURE
**Severity**: 🟠 HIGH
**Files**: `AdminEventController`, `AdminProgramController`, `AdminBookController`

**Issue**:
```java
@PostMapping(consumes = MULTIPART_FORM_DATA)
public ResponseEntity<PostEventResponse> createEvent(
    @RequestPart("event") String eventJson,  // ❌ NO @Valid
    @RequestPart MultipartFile coverImage
) throws Exception {
    PostEventRequest request = objectMapper.readValue(eventJson, PostEventRequest.class);
    // Validation annotations are NOT enforced!
}
```

**Impact**: Validation constraints (@NotBlank, @Size, @Future) are bypassed.

**Remediation**: Manually validate after parsing or restructure to use @ModelAttribute.

---

### 10. Missing Security Headers - CLICKJACKING & XSS
**Severity**: 🟠 HIGH
**Files**: `SecurityConfig.java`, `WebConfig.java`

**Missing Headers**:
- ❌ `Strict-Transport-Security` (HSTS)
- ❌ `X-Frame-Options` (clickjacking protection)
- ❌ `Content-Security-Policy` (XSS mitigation)
- ❌ `X-Content-Type-Options` (MIME sniffing)

**Impact**:
- Clickjacking attacks (embedding site in iframe)
- MIME type confusion attacks
- Reduced XSS protection

**Remediation**:
```java
response.setHeader("X-Frame-Options", "DENY");
response.setHeader("X-Content-Type-Options", "nosniff");
response.setHeader("Content-Security-Policy", "default-src 'self'");
response.setHeader("Strict-Transport-Security", "max-age=31536000");
```

---

### 11. Filename Sanitization Missing - PATH TRAVERSAL
**Severity**: 🟠 HIGH
**File**: R2 file upload implementations

**Issue**: `file.getOriginalFilename()` used without sanitization.

**Attack Example**:
```
POST /api/admin/events
filename: "../../../etc/passwd.jpg"
```

**Remediation**:
```java
public String sanitizeFilename(String filename) {
    return filename.replaceAll("[/\\\\]", "")
                   .replaceAll("[^a-zA-Z0-9._-]", "");
}
```

---

### 12. Weak Password Policy - EASILY GUESSABLE PASSWORDS
**Severity**: 🟠 HIGH
**File**: `PostRegisterRequest.java:17`

**Issue**:
```java
@Size(min = 8)
String password;  // Allows "12345678", "password"
```

**Remediation**:
```java
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$")
String password;
```

---

### 13. Timing Attack in Account Enumeration
**Severity**: 🟠 HIGH
**File**: `PostLoginUseCase.java:38`

**Issue**: BCrypt not called when user doesn't exist → timing difference reveals if email is registered.

**Remediation**:
```java
if (user == null) {
    passwordEncoder.matches(request.password(), "$2a$10$dummy"); // Constant time
    throw new AuthenticationException("Invalid email or password");
}
```

---

## MEDIUM SEVERITY VULNERABILITIES (7)

### 14. CORS Too Permissive - `allowedHeaders("*")`
### 15. Large File Upload Limits - DoS Risk (50MB)
### 16. No HTTPS Enforcement in Spring Config
### 17. Hardcoded CORS Origins (Should use environment variables)
### 18. Catch-All Security Rule - New endpoints default to public
### 19. No Token Binding - Stolen tokens work from any location
### 20. No Issuer Validation in JWT

---

## LOW SEVERITY VULNERABILITIES (3)

### 21. LIKE Query Wildcard Injection - Performance degradation
### 22. No Image Dimension Limits - Image bomb attacks
### 23. Weak Database Password (`sathira`) in .env

---

## SECURITY STRENGTHS ✅

The application demonstrates several excellent security practices:

1. ✅ **BCrypt password hashing** with salt (strength factor 10)
2. ✅ **jOOQ parameterized queries** - No SQL injection vulnerabilities
3. ✅ **Magic byte file validation** - Not just extension checking
4. ✅ **JWT stateless authentication** - Scalable and secure (with strong secret)
5. ✅ **Role-based access control** - Granular permissions with @PreAuthorize
6. ✅ **Global exception handling** - No stack trace leaks to clients
7. ✅ **Short presigned URL expiration** (5-15 minutes)
8. ✅ **@Transactional** usage - Proper transaction management
9. ✅ **Path traversal prevention** - UUID-based filenames
10. ✅ **`.env` in `.gitignore`** - Credentials not committed
11. ✅ **Stateless sessions** - No JSESSIONID cookies
12. ✅ **CSRF disabled** (appropriate for JWT-only auth)
13. ✅ **Explicit field mapping** - No mass assignment vulnerabilities
14. ✅ **No command injection** - No Runtime.exec() or shell commands
15. ✅ **No XXE vulnerabilities** - No XML parsing detected

---

## IMMEDIATE ACTION PLAN

### Priority 1 - TODAY (Block Production Deployment)

1. **Change JWT Secret**:
   ```bash
   openssl rand -base64 64 > /tmp/jwt_secret.txt
   # Update .env with generated secret
   ```

2. **Change Admin Password**:
   - Remove V14 migration OR
   - Force password change on first login OR
   - Document manual admin creation process

3. **Check Git History**:
   ```bash
   git log --all --full-history -- ".env"
   # If credentials found in history, rotate ALL credentials immediately
   ```

4. **Remove/Restrict Hash Generator**:
   ```java
   @Profile("dev")
   @RestController
   public class HashGeneratorController { ... }
   ```

---

### Priority 2 - THIS WEEK

5. **Implement Rate Limiting**:
   - Add Bucket4j dependency
   - Apply to `/api/auth/login`, `/api/auth/refresh`, `/api/auth/register`
   - Configuration: 5 attempts per 15 minutes per IP/email

6. **Add Security Headers**:
   - X-Frame-Options: DENY
   - X-Content-Type-Options: nosniff
   - Strict-Transport-Security (HSTS)
   - Content-Security-Policy

7. **Implement Token Blacklist**:
   - Create `token_blacklist` table
   - Check blacklist in JwtAuthenticationProvider
   - Blacklist tokens on logout

8. **Fix XSS Vulnerabilities**:
   - Add OWASP AntiSamy or Jsoup sanitization
   - Sanitize blog posts, event descriptions, user-generated content

---

### Priority 3 - NEXT 2 WEEKS

9. **Fix Multipart Validation**:
   - Manually validate after ObjectMapper parsing
   - Or restructure to use @ModelAttribute with @Valid

10. **Add Filename Sanitization**:
    - Remove path separators from filenames
    - Whitelist allowed characters

11. **Strengthen Password Policy**:
    - Require 12+ characters
    - Require uppercase, lowercase, digit, special character
    - Consider password strength meter

12. **Configure HTTPS Enforcement**:
    - Add HTTPS redirect in SecurityConfig or reverse proxy

---

## FILES REQUIRING IMMEDIATE CHANGES

### Critical:
1. `/MeditationCenter/.env` - Rotate all credentials
2. `/MeditationCenter/src/main/resources/db/migration/V14__seed_admin_user.sql` - Remove or fix
3. `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/config/SecurityConfig.java` - Add rate limiting, headers, fix catch-all
4. `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/util/HashGeneratorController.java` - Add @Profile("dev")

### High:
5. `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/auth/logout/PostLogoutUseCase.java` - Implement blacklist
6. `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/admin/blog/post/PostBlogPostUseCase.java` - Add XSS sanitization
7. `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/rest/admin/event/AdminEventController.java` - Fix validation
8. `/MeditationCenter/src/main/java/com/isipathana/meditationcenter/client/r2/R2FileManagerClient.java` - Add filename sanitization

---

## VERIFICATION TESTING

After remediation, perform these tests:

### 1. Authentication Security
```bash
# Test rate limiting
for i in {1..10}; do curl -X POST /api/auth/login -d '{"email":"test","password":"test"}'; done
# Expected: 429 Too Many Requests after 5 attempts

# Test JWT with weak secret
# Generate token with old secret, verify it fails

# Test default admin password
curl -X POST /api/auth/login -d '{"email":"admin@meditationcenter.com","password":"admin123"}'
# Expected: 401 Unauthorized or force password change
```

### 2. XSS Protection
```bash
# Test XSS in blog post
curl -X POST /api/admin/blog/post -H "Authorization: Bearer TOKEN" \
  -d '{"title":"<script>alert(1)</script>","content":"test"}'
# Expected: Title sanitized, script tags removed

# Verify response doesn't contain executable JavaScript
```

### 3. Security Headers
```bash
curl -I https://api.iimc.lk/api/programs
# Expected headers:
# X-Frame-Options: DENY
# X-Content-Type-Options: nosniff
# Strict-Transport-Security: max-age=31536000
```

### 4. File Upload Security
```bash
# Test path traversal
curl -F "file=@test.jpg;filename=../../../etc/passwd" /api/admin/events
# Expected: Filename sanitized, no path traversal

# Test malicious file
curl -F "file=@malware.exe" /api/admin/events
# Expected: Rejected by magic byte validation
```

---

## LONG-TERM SECURITY ROADMAP

### Month 1-2:
- Implement refresh token rotation
- Add password complexity requirements
- Enable JWT token binding (IP or device fingerprint)
- Add malware scanning integration (ClamAV or VirusTotal)

### Month 3-4:
- Implement 2FA for admin accounts
- Add email verification before login
- Set up security monitoring and alerting
- Configure R2 bucket encryption and access controls

### Month 5-6:
- Conduct penetration testing
- Implement anomaly detection (concurrent logins from different locations)
- Add password history (prevent reuse)
- Set up bug bounty program

### Ongoing:
- Monthly dependency vulnerability scans (OWASP Dependency-Check)
- Quarterly security audits
- Subscribe to Spring Security advisories
- Regular security training for development team

---

## CONCLUSION

The Meditation Center application has **strong security fundamentals** with proper authentication, authorization, SQL injection protection, and error handling. However, **critical vulnerabilities in secret management, rate limiting, and default credentials** present significant risks that **MUST be resolved before production deployment**.

**Security Score**: 6.5/10 - Good foundation but missing critical protections

**Deployment Recommendation**:
- ❌ **NOT READY for production** until CRITICAL issues resolved
- ✅ **Ready after remediation** of Priority 1 & 2 items (estimated 1 week)
- 🎯 **Target score after fixes**: 8.5/10

With proper remediation following this plan, the application will have enterprise-grade security suitable for production deployment.
