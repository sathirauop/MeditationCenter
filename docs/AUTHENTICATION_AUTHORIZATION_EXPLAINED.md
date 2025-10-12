# Complete Authentication & Authorization System - Detailed Explanation

## Table of Contents
1. [System Overview](#system-overview)
2. [Authentication vs Authorization](#authentication-vs-authorization)
3. [JWT (JSON Web Token) Explained](#jwt-json-web-token-explained)
4. [Complete Request Flow](#complete-request-flow)
5. [Component-by-Component Breakdown](#component-by-component-breakdown)
6. [Security Architecture](#security-architecture)
7. [Role & Permission System](#role--permission-system)
8. [How to Use in Your Code](#how-to-use-in-your-code)
9. [Testing the System](#testing-the-system)

---

## System Overview

Your MeditationCenter application now has a **complete, production-ready authentication and authorization system** using:

- **JWT (JSON Web Tokens)** for stateless authentication
- **Spring Security** as the security framework
- **BCrypt** for password hashing
- **Hybrid Role + Permission-Based Access Control** (3 roles, 29 permissions)
- **Access tokens** (15 minutes) + **Refresh tokens** (7 days)

### What Does This Mean?

**Before:** Your API endpoints were open to anyone. Anyone could access any endpoint without proving who they are.

**Now:**
- Users must **register** and **login** to get tokens
- Protected endpoints require valid JWT tokens
- System verifies WHO you are (authentication)
- System checks WHAT you can do (authorization)
- Tokens expire for security
- Passwords are never stored in plain text

---

## Authentication vs Authorization

### **Authentication = WHO are you?**
- Proving your identity
- Like showing your ID card at the door
- In our system: Login with email + password → Get JWT token
- JWT token proves "I am user #123, email: john@example.com, role: USER"

### **Authorization = WHAT can you do?**
- Checking permissions after identity is verified
- Like checking if your ID card allows access to VIP room
- In our system: JWT contains your role → Spring Security checks if your role has permission for the endpoint

**Example:**
```
Authentication: "I'm John Doe, here's my JWT token" ✅ Valid
Authorization: "Can John Doe delete programs?" → Check role → USER role → ❌ Denied (only ADMIN can delete)
```

---

## JWT (JSON Web Token) Explained

### What is JWT?

JWT is a **secure, self-contained token** that carries information about the user. Think of it as a **digitally signed ID card** that can't be forged.

### JWT Structure

A JWT has 3 parts separated by dots:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEyMywicm9sZSI6IlVTRVIifQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
│                                    │                                      │
│                                    │                                      └─ SIGNATURE (verification)
│                                    └─ PAYLOAD (user data)
└─ HEADER (algorithm)
```

### JWT Payload (What's Inside)

```json
{
  "userId": 123,
  "email": "john@example.com",
  "role": "USER",
  "type": "ACCESS",
  "iss": "meditation-center",
  "iat": 1234567890,  // Issued at
  "exp": 1234568790   // Expires at (15 min later)
}
```

### Why JWT?

✅ **Stateless:** Server doesn't need to store sessions (scalable)
✅ **Self-contained:** Token contains all user info (no database lookup on every request)
✅ **Secure:** Digitally signed (can't be tampered with)
✅ **Verifiable:** Server verifies signature using secret key
❌ **Can't be revoked:** Once issued, valid until expiration (solved with short expiration + refresh tokens)

### Access Token vs Refresh Token

| Feature | Access Token | Refresh Token |
|---------|-------------|---------------|
| **Purpose** | Access protected resources | Get new access token |
| **Lifetime** | 15 minutes (short) | 7 days (long) |
| **Usage** | Every API request | Only to /api/auth/refresh |
| **Contains** | userId, email, role | userId only |
| **Security** | Stored in memory | Stored securely (httpOnly cookie) |
| **If stolen** | Limited damage (expires soon) | More dangerous (can get new access tokens) |

**Why two tokens?**
- Short-lived access token = Less risk if stolen
- Long-lived refresh token = User doesn't need to login every 15 minutes
- Best of both worlds: Security + Convenience

---

## Complete Request Flow

### **Flow 1: User Registration**

```
┌─────────────┐
│   Client    │
│ (Frontend)  │
└──────┬──────┘
       │
       │ 1. POST /api/auth/register
       │    { email, password, name, mobileNumber }
       ↓
┌──────────────────────────────────────────┐
│        AuthController.register()         │
│  - Receives RegisterRequest              │
│  - Validates input (@Valid)              │
└──────┬───────────────────────────────────┘
       │
       │ 2. Calls authService.register()
       ↓
┌──────────────────────────────────────────┐
│         AuthService.register()           │
│  - Check if email exists                 │
│  - Hash password with BCrypt             │
│  - Save user to database                 │
│  - Generate access token (15 min)        │
│  - Generate refresh token (7 days)       │
└──────┬───────────────────────────────────┘
       │
       │ 3. userRepository.create(user)
       ↓
┌──────────────────────────────────────────┐
│     UserRepository.create()              │
│  - INSERT INTO users (...)               │
│  - Returns saved user                    │
└──────┬───────────────────────────────────┘
       │
       │ 4. jwtService.generateAccessToken()
       ↓
┌──────────────────────────────────────────┐
│      JwtService.generateAccessToken()    │
│  - Create JWT with userId, email, role   │
│  - Sign with secret key (HMAC-SHA256)    │
│  - Set expiration (15 min)               │
└──────┬───────────────────────────────────┘
       │
       │ 5. Return tokens
       ↓
┌──────────────────────────────────────────┐
│            Response                      │
│  {                                       │
│    "access_token": "eyJ...",             │
│    "refresh_token": "eyJ...",            │
│    "token_type": "Bearer",               │
│    "expires_in": 900                     │
│  }                                       │
└──────────────────────────────────────────┘
```

### **Flow 2: User Login**

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. POST /api/auth/login
       │    { email, password }
       ↓
┌──────────────────────────────────────────┐
│         AuthController.login()           │
└──────┬───────────────────────────────────┘
       │
       │ 2. authService.login()
       ↓
┌──────────────────────────────────────────┐
│          AuthService.login()             │
│  - Find user by email (with password)    │
│  - Verify password with BCrypt           │
│  - Check if account is active            │
│  - Generate tokens                       │
└──────┬───────────────────────────────────┘
       │
       │ 3. passwordEncoder.matches()
       ↓
┌──────────────────────────────────────────┐
│      BCryptPasswordEncoder               │
│  - Compare plain password with hash      │
│  - Returns true/false                    │
└──────┬───────────────────────────────────┘
       │
       │ 4. Return tokens (same as registration)
       ↓
```

### **Flow 3: Accessing Protected Endpoint**

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. GET /api/bookings/my
       │    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
       ↓
┌──────────────────────────────────────────────────────────┐
│              JwtAuthenticationFilter                     │
│  (OncePerRequestFilter - runs on EVERY request)          │
│                                                          │
│  1. Extract JWT from "Authorization: Bearer {token}"    │
│  2. Check if JWT exists                                 │
│  3. Create unauthenticated JwtAuthenticationToken       │
│  4. Pass to AuthenticationManager                       │
└──────┬───────────────────────────────────────────────────┘
       │
       │ 2. authenticationManager.authenticate()
       ↓
┌──────────────────────────────────────────────────────────┐
│          AuthenticationManager                           │
│  (Delegates to registered providers)                     │
└──────┬───────────────────────────────────────────────────┘
       │
       │ 3. jwtAuthenticationProvider.authenticate()
       ↓
┌──────────────────────────────────────────────────────────┐
│        JwtAuthenticationProvider                         │
│  1. Validate JWT signature                              │
│  2. Check if token is expired                           │
│  3. Verify token type (ACCESS not REFRESH)              │
│  4. Extract userId, email, role from JWT                │
│  5. Load user from database (verify still active)       │
│  6. Create MeditationCenterUser (principal)             │
│  7. Return authenticated token with authorities         │
└──────┬───────────────────────────────────────────────────┘
       │
       │ 4. Store in SecurityContext
       ↓
┌──────────────────────────────────────────────────────────┐
│            SecurityContextHolder                         │
│  - Stores authenticated user for this request           │
│  - Available throughout request lifecycle               │
│  - Cleared after response sent                          │
└──────┬───────────────────────────────────────────────────┘
       │
       │ 5. Continue to controller
       ↓
┌──────────────────────────────────────────────────────────┐
│         BookingController.getMyBookings()                │
│  @GetMapping("/my")                                      │
│  public List<Booking> getMyBookings(                     │
│      @AuthenticationPrincipal MeditationCenterUser user) │
│  {                                                       │
│      Long userId = user.getUserId();                     │
│      return bookingService.getBookingsByUserId(userId);  │
│  }                                                       │
└──────┬───────────────────────────────────────────────────┘
       │
       │ 6. Return response
       ↓
┌─────────────┐
│   Client    │
└─────────────┘
```

### **Flow 4: Refresh Token Flow**

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. Access token expired (after 15 min)
       │ 2. POST /api/auth/refresh
       │    { refreshToken: "eyJ..." }
       ↓
┌──────────────────────────────────────────┐
│      AuthController.refresh()            │
└──────┬───────────────────────────────────┘
       │
       │ 2. authService.refresh()
       ↓
┌──────────────────────────────────────────┐
│       AuthService.refresh()              │
│  - Validate refresh token                │
│  - Check token type (REFRESH)            │
│  - Extract userId from token             │
│  - Load user from DB (verify active)     │
│  - Generate NEW access token (15 min)    │
│  - Do NOT generate new refresh token     │
└──────┬───────────────────────────────────┘
       │
       │ 3. Return new access token
       ↓
┌──────────────────────────────────────────┐
│            Response                      │
│  {                                       │
│    "access_token": "eyJ...",  (NEW)      │
│    "refresh_token": null,                │
│    "token_type": "Bearer",               │
│    "expires_in": 900                     │
│  }                                       │
└──────────────────────────────────────────┘
       │
       │ 4. Client uses new access token
       ↓
```

---

## Component-by-Component Breakdown

### **1. DTOs (Data Transfer Objects)**

Location: `src/main/java/com/isipathana/meditationcenter/auth/dto/`

#### **LoginRequest.java**
```java
public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password
)
```
**Purpose:** Carries login data from client to server
**Validation:**
- Email must be present and valid format
- Password must be present
- If validation fails → 400 Bad Request with error details

#### **RegisterRequest.java**
```java
public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank @Size(min = 2, max = 255) String name,
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") String mobileNumber
)
```
**Purpose:** Carries registration data
**Validation:**
- Email: Required, valid format
- Password: Required, minimum 8 characters
- Name: Required, 2-255 characters
- Mobile: Optional, international format (+94771234567)

#### **RefreshTokenRequest.java**
```java
public record RefreshTokenRequest(
    @NotBlank String refreshToken
)
```
**Purpose:** Carries refresh token for token refresh operation

#### **AuthenticationResponse.java**
```java
public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn
)
```
**Purpose:** Standard response for login/register/refresh
**Fields:**
- `access_token`: JWT for accessing protected resources
- `refresh_token`: JWT for getting new access token (null on refresh)
- `token_type`: Always "Bearer"
- `expires_in`: Seconds until access token expires (900 = 15 min)

---

### **2. AuthService**

Location: `src/main/java/com/isipathana/meditationcenter/auth/service/AuthService.java`

**Purpose:** Business logic for authentication operations

#### **register() Method**
```java
public AuthenticationResponse register(RegisterRequest request)
```
**What it does:**
1. Check if email already exists → Throw ConflictException (409)
2. Hash password using BCrypt (10 rounds, salt automatically added)
3. Create User object with default role (USER), active status (true), email verified (false)
4. Save to database
5. Generate access token (15 min) with userId, email, role
6. Generate refresh token (7 days) with userId only
7. Return both tokens

**Why hash password?**
- Never store plain text passwords (security best practice)
- BCrypt uses salt (random data) → Same password = Different hash
- BCrypt is slow by design → Prevents brute force attacks
- One-way function → Can't get password from hash

#### **login() Method**
```java
public AuthenticationResponse login(LoginRequest request)
```
**What it does:**
1. Find user by email (with password field)
2. If user not found → Throw AuthenticationException (401) "Invalid email or password"
3. Verify password: `passwordEncoder.matches(plainPassword, hashedPassword)`
4. If password wrong → Throw AuthenticationException (401) "Invalid email or password"
5. Check if account is active → If not, throw exception
6. Convert UserRole enum to Role enum (database enum → security enum)
7. Generate tokens
8. Return tokens

**Security note:** Same error message for "user not found" and "wrong password" → Prevents email enumeration attacks

#### **refresh() Method**
```java
public AuthenticationResponse refresh(RefreshTokenRequest request)
```
**What it does:**
1. Validate refresh token signature and expiration
2. Check token type must be "REFRESH" (not "ACCESS")
3. Extract userId from token
4. Load user from database (verify still exists and active)
5. Generate NEW access token with updated user info
6. Return new access token (no new refresh token)

**Why no new refresh token?**
- Simpler implementation
- Refresh token lives 7 days (long enough)
- Future enhancement: Refresh token rotation (new refresh token on each refresh for better security)

---

### **3. AuthController**

Location: `src/main/java/com/isipathana/meditationcenter/auth/controller/AuthController.java`

**Purpose:** REST API endpoints for authentication

#### **POST /api/auth/register**
```java
@PostMapping("/register")
public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request)
```
- **Public endpoint** (no authentication required)
- **@Valid** triggers validation → If fails, GlobalExceptionHandler catches it
- Returns **201 Created** on success
- Returns **400 Bad Request** on validation errors
- Returns **409 Conflict** if email already exists

#### **POST /api/auth/login**
```java
@PostMapping("/login")
public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request)
```
- **Public endpoint**
- Returns **200 OK** on success
- Returns **400 Bad Request** on validation errors
- Returns **401 Unauthorized** on invalid credentials

#### **POST /api/auth/refresh**
```java
@PostMapping("/refresh")
public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request)
```
- **Public endpoint**
- Returns **200 OK** with new access token
- Returns **401 Unauthorized** if refresh token invalid

#### **POST /api/auth/logout**
```java
@PostMapping("/logout")
public ResponseEntity<Void> logout()
```
- Currently does nothing (JWT is stateless)
- Client deletes tokens (client-side logout)
- Future enhancement: Token blacklist (server-side revocation)

---

### **4. JwtService**

Location: `src/main/java/com/isipathana/meditationcenter/security/jwt/JwtService.java`

**Purpose:** Generate and validate JWT tokens

#### **generateAccessToken()**
```java
public String generateAccessToken(Long userId, String email, Role role)
```
**Creates JWT with:**
- Claims: userId, email, role, type="ACCESS"
- Subject: email
- Issuer: "meditation-center"
- Issued at: current time
- Expiration: current time + 15 minutes
- Signature: HMAC-SHA256 with secret key

**Signature algorithm (HMAC-SHA256):**
```
signature = HMAC-SHA256(
    base64(header) + "." + base64(payload),
    secret_key
)
```
- Server signs with secret key
- Anyone can read payload (it's just base64 encoded)
- Only server can verify signature (only server has secret key)
- If token tampered → Signature won't match → Token rejected

#### **generateRefreshToken()**
```java
public String generateRefreshToken(Long userId, String email)
```
- Similar to access token but:
  - type="REFRESH"
  - No role (not used for authorization)
  - Expiration: 7 days

#### **validateToken()**
```java
public boolean validateToken(String token)
```
**Validates:**
1. Token format is valid
2. Signature matches (proves token came from us)
3. Token not expired
4. Token not tampered with

**How signature verification works:**
```
1. Extract header and payload from token
2. Compute signature: HMAC-SHA256(header.payload, secret_key)
3. Compare computed signature with signature in token
4. If match → Valid ✅
5. If different → Invalid/Tampered ❌
```

#### **extractUserId(), extractEmail(), extractRole(), extractTokenType()**
Extract claims from JWT payload (already verified)

---

### **5. JwtAuthenticationFilter**

Location: `src/main/java/com/isipathana/meditationcenter/security/jwt/JwtAuthenticationFilter.java`

**Purpose:** Intercepts EVERY request to extract and validate JWT

**Extends:** `OncePerRequestFilter` (Spring Security)

#### **How it works:**

```java
protected void doFilterInternal(HttpServletRequest request,
                               HttpServletResponse response,
                               FilterChain filterChain)
```

**Step-by-step:**

1. **Extract JWT from request header**
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                  ^^^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                  Prefix  JWT token
   ```
   - Looks for "Authorization" header
   - Checks if starts with "Bearer "
   - Extracts token part

2. **If no JWT found:**
   - Continue filter chain (request proceeds)
   - Will hit SecurityConfig rules
   - If endpoint requires auth → 401 Unauthorized
   - If endpoint public → Proceeds normally

3. **If JWT found:**
   - Create `JwtAuthenticationToken` (unauthenticated state)
   - Pass to `AuthenticationManager`
   - Manager delegates to `JwtAuthenticationProvider`
   - Provider validates and returns authenticated token
   - Store authenticated token in `SecurityContextHolder`

4. **If authentication fails:**
   - Clear `SecurityContextHolder`
   - Log warning
   - Continue filter chain
   - SecurityConfig rules will reject request → 401 Unauthorized

5. **Continue filter chain**
   - Request proceeds to next filter or controller
   - Authenticated user available via `SecurityContextHolder`

**Why OncePerRequestFilter?**
- Ensures filter runs exactly once per request
- Prevents double-processing on forwards/includes
- Spring Security best practice

---

### **6. JwtAuthenticationProvider**

Location: `src/main/java/com/isipathana/meditationcenter/security/jwt/JwtAuthenticationProvider.java`

**Purpose:** Validates JWT and creates authenticated user

#### **authenticate() Method**

```java
public Authentication authenticate(Authentication authentication)
```

**Step-by-step:**

1. **Cast to JwtAuthenticationToken**
   ```java
   JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
   String jwt = jwtAuth.getJwt();
   ```

2. **Validate JWT**
   ```java
   if (!jwtService.validateToken(jwt)) {
       throw new AuthenticationException("Invalid or expired JWT token");
   }
   ```
   - Checks signature
   - Checks expiration
   - Checks format

3. **Verify token type**
   ```java
   String tokenType = jwtService.extractTokenType(jwt);
   if (!"ACCESS".equals(tokenType)) {
       throw new AuthenticationException("Invalid token type. Expected ACCESS token.");
   }
   ```
   - Prevents using refresh token for accessing resources
   - Refresh token only for /api/auth/refresh

4. **Extract user info from JWT**
   ```java
   Long userId = jwtService.extractUserId(jwt);
   String email = jwtService.extractEmail(jwt);
   Role role = jwtService.extractRole(jwt);
   ```

5. **Load user from database**
   ```java
   var user = userRepository.findById(userId);
   ```
   **Why check database if JWT is valid?**
   - Account might be deactivated after token issued
   - User might be deleted
   - Role might have changed (rare but possible)
   - Best practice: Verify current status

6. **Verify account is active**
   ```java
   if (user.isActive() == null || !user.isActive()) {
       throw new AuthenticationException("User account is inactive");
   }
   ```

7. **Create MeditationCenterUser (Principal)**
   ```java
   MeditationCenterUser principal = MeditationCenterUser.builder()
       .userId(userId)
       .email(email)
       .name(user.name())
       .role(role)
       .isActive(user.isActive())
       .emailVerified(user.emailVerified())
       .build();
   ```
   - Principal = Authenticated user object
   - Available in controllers via `@AuthenticationPrincipal`

8. **Return authenticated token**
   ```java
   return new JwtAuthenticationToken(jwt, principal, principal.getAuthorities());
   ```
   - Authorities = Role + Permissions
   - Used by Spring Security for authorization checks

---

### **7. JwtAuthenticationToken**

Location: `src/main/java/com/isipathana/meditationcenter/security/jwt/JwtAuthenticationToken.java`

**Purpose:** Represents authentication state (unauthenticated → authenticated)

#### **Two States:**

**State 1: Unauthenticated**
```java
public JwtAuthenticationToken(String jwt) {
    super(null);  // No authorities
    this.jwt = jwt;
    this.principal = null;
    setAuthenticated(false);  // Not authenticated yet
}
```
- Created by `JwtAuthenticationFilter`
- Contains JWT only
- No principal, no authorities
- Used to pass JWT to `AuthenticationManager`

**State 2: Authenticated**
```java
public JwtAuthenticationToken(String jwt, Object principal,
                              Collection<? extends GrantedAuthority> authorities) {
    super(authorities);  // Role + Permissions
    this.jwt = jwt;
    this.principal = principal;  // MeditationCenterUser
    setAuthenticated(true);  // Now authenticated
}
```
- Created by `JwtAuthenticationProvider` after validation
- Contains JWT, principal, authorities
- Stored in `SecurityContextHolder`
- Available throughout request

---

### **8. MeditationCenterUser (Principal)**

Location: `src/main/java/com/isipathana/meditationcenter/security/MeditationCenterUser.java`

**Purpose:** Represents authenticated user (implements Spring Security's `UserDetails`)

```java
@Getter
@Builder
public class MeditationCenterUser implements UserDetails {
    private final Long userId;
    private final String email;
    private final String name;
    private final Role role;
    private final Boolean isActive;
    private final Boolean emailVerified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(role);  // Add role (e.g., ROLE_USER)
        authorities.addAll(role.getPermissions());  // Add permissions (e.g., VIEW_PROGRAMS)
        return authorities;
    }

    // Other UserDetails methods...
}
```

#### **getAuthorities() Explained**

Returns **role + all permissions** for that role:

**Example for USER role:**
```
Authorities:
- ROLE_USER (role)
- VIEW_PROGRAMS (permission)
- CREATE_BOOKING (permission)
- VIEW_OWN_BOOKINGS (permission)
- CANCEL_OWN_BOOKING (permission)
- UPDATE_OWN_PROFILE (permission)
- VIEW_EVENTS (permission)
- REGISTER_FOR_EVENT (permission)
```

**Spring Security uses this for authorization:**
- `@PreAuthorize("hasRole('USER')")` → Checks if "ROLE_USER" in authorities
- `@PreAuthorize("hasAuthority('VIEW_PROGRAMS')")` → Checks if "VIEW_PROGRAMS" in authorities

#### **Helper Methods**

```java
public boolean hasRole(Role role) {
    return this.role == role;
}

public boolean hasPermission(Permission permission) {
    return this.role.getPermissions().contains(permission);
}
```

**Usage in code:**
```java
if (user.hasRole(Role.ADMIN)) {
    // Do admin stuff
}

if (user.hasPermission(Permission.DELETE_PROGRAM)) {
    // Delete program
}
```

---

### **9. Role Enum**

Location: `src/main/java/com/isipathana/meditationcenter/security/Role.java`

**Purpose:** Defines user roles and their permissions

```java
public enum Role implements GrantedAuthority {
    USER(Set.of(
        Permission.VIEW_PROGRAMS,
        Permission.CREATE_BOOKING,
        Permission.VIEW_OWN_BOOKINGS,
        Permission.CANCEL_OWN_BOOKING,
        Permission.UPDATE_OWN_PROFILE,
        Permission.VIEW_EVENTS,
        Permission.REGISTER_FOR_EVENT
    )),

    INSTRUCTOR(Set.of(
        Permission.VIEW_PROGRAMS,
        Permission.VIEW_ASSIGNED_PROGRAMS,
        Permission.MARK_ATTENDANCE,
        Permission.VIEW_STUDENTS,
        Permission.UPDATE_OWN_PROFILE
    )),

    ADMIN(Set.of(
        // All 22 permissions - full access
    ));

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();  // "ROLE_USER", "ROLE_ADMIN", etc.
    }
}
```

#### **Why "ROLE_" prefix?**

Spring Security convention:
- Roles have "ROLE_" prefix
- `hasRole('USER')` automatically adds prefix → Checks for "ROLE_USER"
- `hasAuthority('ROLE_USER')` requires full string
- Both work, but `hasRole()` is cleaner

---

### **10. Permission Enum**

Location: `src/main/java/com/isipathana/meditationcenter/security/Permission.java`

**Purpose:** Fine-grained permissions (29 total)

```java
public enum Permission implements GrantedAuthority {
    // Program permissions
    VIEW_PROGRAMS,
    CREATE_PROGRAM,
    UPDATE_PROGRAM,
    DELETE_PROGRAM,
    VIEW_ASSIGNED_PROGRAMS,

    // Booking permissions
    VIEW_OWN_BOOKINGS,
    VIEW_ALL_BOOKINGS,
    CREATE_BOOKING,
    CANCEL_OWN_BOOKING,
    CANCEL_ANY_BOOKING,

    // User permissions
    VIEW_USERS,
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,
    UPDATE_OWN_PROFILE,

    // Instructor permissions
    ASSIGN_INSTRUCTOR,
    MARK_ATTENDANCE,
    VIEW_STUDENTS,

    // Event permissions
    VIEW_EVENTS,
    CREATE_EVENT,
    UPDATE_EVENT,
    DELETE_EVENT,
    REGISTER_FOR_EVENT,
    VIEW_EVENT_REGISTRATIONS,

    // Reporting
    EXPORT_DATA,
    VIEW_REPORTS,
    VIEW_ANALYTICS,
    MANAGE_DONATIONS;

    @Override
    public String getAuthority() {
        return this.name();  // "VIEW_PROGRAMS", "CREATE_PROGRAM", etc.
    }
}
```

---

### **11. SecurityConfig**

Location: `src/main/java/com/isipathana/meditationcenter/config/SecurityConfig.java`

**Purpose:** Configure Spring Security

#### **securityFilterChain() Bean**

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
```

**Configuration breakdown:**

**1. Disable CSRF**
```java
.csrf(AbstractHttpConfigurer::disable)
```
**Why?**
- CSRF protection for cookie-based sessions
- JWT is stateless (no cookies)
- CSRF not needed for JWT

**2. Authorization Rules**
```java
.authorizeHttpRequests(auth -> auth
    // Public endpoints
    .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/programs").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/programs/{id}").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/events").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/events/{id}").permitAll()
    .requestMatchers("/error").permitAll()

    // Protected endpoints
    .requestMatchers("/api/**").authenticated()

    // Everything else
    .anyRequest().permitAll()
)
```

**How it works:**
- Rules evaluated **top to bottom**
- First match wins
- `permitAll()` = No authentication required
- `authenticated()` = Must have valid JWT
- `/api/auth/**` public → Anyone can register/login
- `/api/programs` public → Anyone can view programs
- All other `/api/**` protected → Requires valid JWT

**3. Stateless Session Management**
```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```
**Why stateless?**
- No server-side sessions
- No session cookies
- Each request independent
- Scalable (no session storage needed)
- JWT contains all needed info

**4. Exception Handling**
```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint(authenticationEntryPoint)  // 401
    .accessDeniedHandler(accessDeniedHandler)            // 403
)
```
- 401 Unauthorized: Authentication failed (no token, invalid token, expired token)
- 403 Forbidden: Authorization failed (authenticated but no permission)

**5. Add JWT Filter**
```java
.addFilterBefore(
    jwtAuthenticationFilter(),
    UsernamePasswordAuthenticationFilter.class
)
```
- JWT filter runs **before** username/password filter
- Extracts and validates JWT on every request
- Populates `SecurityContextHolder` if valid

#### **Other Beans**

**AuthenticationManager**
```java
@Bean
public AuthenticationManager authenticationManager() {
    return new ProviderManager(jwtAuthenticationProvider);
}
```
- Delegates authentication to `JwtAuthenticationProvider`

**PasswordEncoder**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
- BCrypt with default strength (10 rounds)
- Used for hashing passwords on registration
- Used for verifying passwords on login

---

### **12. JwtAuthenticationEntryPoint (401 Handler)**

Location: `src/main/java/com/isipathana/meditationcenter/security/JwtAuthenticationEntryPoint.java`

**Purpose:** Handle authentication failures (401 Unauthorized)

**Triggered when:**
- No JWT provided but endpoint requires authentication
- JWT is invalid (bad signature)
- JWT is expired
- User not found in database
- Account is inactive

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required. Please provide a valid JWT token.",
  "path": "/api/bookings/my"
}
```

---

### **13. JwtAccessDeniedHandler (403 Handler)**

Location: `src/main/java/com/isipathana/meditationcenter/security/JwtAccessDeniedHandler.java`

**Purpose:** Handle authorization failures (403 Forbidden)

**Triggered when:**
- User is authenticated but lacks required role
- User is authenticated but lacks required permission
- `@PreAuthorize` check fails

**Response:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied. You don't have permission to access this resource.",
  "path": "/api/programs"
}
```

---

### **14. JwtProperties**

Location: `src/main/java/com/isipathana/meditationcenter/config/JwtProperties.java`

**Purpose:** Load JWT configuration from application.properties

```java
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long accessTokenExpiration = 900000L;      // 15 min in ms
    private Long refreshTokenExpiration = 604800000L;  // 7 days in ms
    private String issuer = "meditation-center";
}
```

**Loaded from application.properties:**
```properties
jwt.secret=change-this-to-a-secure-256-bit-secret-key-in-production-environment
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000
jwt.issuer=meditation-center
```

**⚠️ PRODUCTION SECURITY:**
- Change `jwt.secret` to strong random key (256 bits minimum)
- Store secret in environment variable, not in code
- Never commit secret to Git

---

## Security Architecture

### **Security Layers**

```
┌─────────────────────────────────────────────────────────┐
│                      Client                             │
│  - Stores access token (memory)                         │
│  - Stores refresh token (secure storage)                │
│  - Adds "Authorization: Bearer {token}" to requests     │
└───────────────────────┬─────────────────────────────────┘
                        │
                        │ HTTPS (encrypted)
                        ↓
┌─────────────────────────────────────────────────────────┐
│                   Layer 1: Network                      │
│  - TLS/SSL encryption                                   │
│  - HTTPS only in production                             │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────┐
│            Layer 2: JwtAuthenticationFilter             │
│  - Extract JWT from header                              │
│  - Pass to AuthenticationManager                        │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────┐
│          Layer 3: JwtAuthenticationProvider             │
│  - Validate JWT signature                               │
│  - Check expiration                                     │
│  - Verify token type                                    │
│  - Load user from database                              │
│  - Verify account status                                │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────┐
│              Layer 4: SecurityConfig                    │
│  - Check if endpoint requires authentication            │
│  - If yes and no auth → 401 Unauthorized                │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────┐
│            Layer 5: Method Security                     │
│  - @PreAuthorize checks                                 │
│  - Role-based: hasRole('ADMIN')                         │
│  - Permission-based: hasAuthority('DELETE_PROGRAM')     │
│  - If fails → 403 Forbidden                             │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────────┐
│                Layer 6: Controller                      │
│  - Business logic                                       │
│  - Access authenticated user via @AuthenticationPrincipal│
└─────────────────────────────────────────────────────────┘
```

---

## Role & Permission System

### **Role Hierarchy**

```
ADMIN (Superuser)
  ├─ All 22 permissions
  ├─ Can do everything
  └─ Manages entire system

INSTRUCTOR (Staff)
  ├─ 5 permissions
  ├─ VIEW_PROGRAMS
  ├─ VIEW_ASSIGNED_PROGRAMS
  ├─ MARK_ATTENDANCE
  ├─ VIEW_STUDENTS
  └─ UPDATE_OWN_PROFILE

USER (Regular)
  ├─ 7 permissions
  ├─ VIEW_PROGRAMS
  ├─ CREATE_BOOKING
  ├─ VIEW_OWN_BOOKINGS
  ├─ CANCEL_OWN_BOOKING
  ├─ UPDATE_OWN_PROFILE
  ├─ VIEW_EVENTS
  └─ REGISTER_FOR_EVENT
```

### **Permission Matrix**

| Permission | USER | INSTRUCTOR | ADMIN |
|------------|------|------------|-------|
| VIEW_PROGRAMS | ✅ | ✅ | ✅ |
| CREATE_PROGRAM | ❌ | ❌ | ✅ |
| UPDATE_PROGRAM | ❌ | ❌ | ✅ |
| DELETE_PROGRAM | ❌ | ❌ | ✅ |
| VIEW_ASSIGNED_PROGRAMS | ❌ | ✅ | ✅ |
| CREATE_BOOKING | ✅ | ❌ | ✅ |
| VIEW_OWN_BOOKINGS | ✅ | ❌ | ✅ |
| VIEW_ALL_BOOKINGS | ❌ | ❌ | ✅ |
| CANCEL_OWN_BOOKING | ✅ | ❌ | ✅ |
| CANCEL_ANY_BOOKING | ❌ | ❌ | ✅ |
| UPDATE_OWN_PROFILE | ✅ | ✅ | ✅ |
| VIEW_USERS | ❌ | ❌ | ✅ |
| CREATE_USER | ❌ | ❌ | ✅ |
| UPDATE_USER | ❌ | ❌ | ✅ |
| DELETE_USER | ❌ | ❌ | ✅ |
| MARK_ATTENDANCE | ❌ | ✅ | ✅ |
| VIEW_STUDENTS | ❌ | ✅ | ✅ |
| ASSIGN_INSTRUCTOR | ❌ | ❌ | ✅ |
| VIEW_EVENTS | ✅ | ❌ | ✅ |
| CREATE_EVENT | ❌ | ❌ | ✅ |
| UPDATE_EVENT | ❌ | ❌ | ✅ |
| DELETE_EVENT | ❌ | ❌ | ✅ |
| REGISTER_FOR_EVENT | ✅ | ❌ | ✅ |
| VIEW_EVENT_REGISTRATIONS | ❌ | ❌ | ✅ |
| EXPORT_DATA | ❌ | ❌ | ✅ |
| VIEW_REPORTS | ❌ | ❌ | ✅ |
| VIEW_ANALYTICS | ❌ | ❌ | ✅ |
| MANAGE_DONATIONS | ❌ | ❌ | ✅ |

---

## How to Use in Your Code

### **1. Access Authenticated User**

```java
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    @GetMapping("/my")
    public List<Booking> getMyBookings(
            @AuthenticationPrincipal MeditationCenterUser user) {

        // Access user information
        Long userId = user.getUserId();
        String email = user.getEmail();
        String name = user.getName();
        Role role = user.getRole();

        // Use in your logic
        return bookingService.getBookingsByUserId(userId);
    }
}
```

### **2. Role-Based Authorization**

```java
// Only ADMIN can access
@DeleteMapping("/programs/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void deleteProgram(@PathVariable Long id) {
    programService.delete(id);
}

// ADMIN or INSTRUCTOR can access
@GetMapping("/programs/assigned")
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
public List<Program> getAssignedPrograms(
        @AuthenticationPrincipal MeditationCenterUser user) {
    return programService.getAssignedPrograms(user.getUserId());
}
```

### **3. Permission-Based Authorization**

```java
// Only users with CREATE_PROGRAM permission
@PostMapping("/programs")
@PreAuthorize("hasAuthority('CREATE_PROGRAM')")
public Program createProgram(@RequestBody ProgramDTO dto) {
    return programService.create(dto);
}

// Multiple permissions (OR logic)
@GetMapping("/analytics")
@PreAuthorize("hasAuthority('VIEW_ANALYTICS') or hasAuthority('VIEW_REPORTS')")
public Analytics getAnalytics() {
    return analyticsService.getAnalytics();
}
```

### **4. Resource-Based Authorization (Owner Check)**

```java
@PutMapping("/users/{id}")
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
public User updateUser(
        @PathVariable Long id,
        @RequestBody UserUpdateDTO dto,
        @AuthenticationPrincipal MeditationCenterUser user) {
    // ADMIN can update any user
    // Regular users can only update their own profile
    return userService.update(id, dto);
}
```

### **5. Programmatic Authorization (Manual Check)**

```java
@PutMapping("/bookings/{id}")
public Booking updateBooking(
        @PathVariable Long id,
        @RequestBody BookingUpdateDTO dto,
        @AuthenticationPrincipal MeditationCenterUser user) {

    Booking booking = bookingService.findById(id);

    // Manual permission check
    if (!user.hasRole(Role.ADMIN) && !booking.getUserId().equals(user.getUserId())) {
        throw new ForbiddenException("You can only update your own bookings");
    }

    return bookingService.update(id, dto);
}
```

### **6. Check Authentication in Service Layer**

```java
@Service
@RequiredArgsConstructor
public class BookingService {

    public void cancelBooking(Long bookingId, MeditationCenterUser user) {
        Booking booking = findById(bookingId);

        // Check if user is owner or admin
        boolean isOwner = booking.getUserId().equals(user.getUserId());
        boolean isAdmin = user.hasRole(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Cannot cancel this booking");
        }

        // Proceed with cancellation
        booking.setStatus(BookingStatus.CANCELLED);
        save(booking);
    }
}
```

---

## Testing the System

### **Test 1: Register New User**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "name": "John Doe",
    "mobileNumber": "+94771234567"
  }'
```

**Expected Response (201 Created):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

### **Test 2: Login**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

### **Test 3: Access Public Endpoint (No Auth)**

```bash
curl -X GET http://localhost:8080/api/programs
```

**Expected:** Success (programs list)

### **Test 4: Access Protected Endpoint (No Auth)**

```bash
curl -X GET http://localhost:8080/api/bookings/my
```

**Expected Response (401 Unauthorized):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required. Please provide a valid JWT token.",
  "path": "/api/bookings/my"
}
```

### **Test 5: Access Protected Endpoint (With Auth)**

```bash
# Replace {ACCESS_TOKEN} with actual token from login
curl -X GET http://localhost:8080/api/bookings/my \
  -H "Authorization: Bearer {ACCESS_TOKEN}"
```

**Expected:** Success (user's bookings)

### **Test 6: Access Admin Endpoint as USER**

```bash
# Login as regular user, then try admin endpoint
curl -X DELETE http://localhost:8080/api/programs/1 \
  -H "Authorization: Bearer {USER_ACCESS_TOKEN}"
```

**Expected Response (403 Forbidden):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied. You don't have permission to access this resource.",
  "path": "/api/programs/1"
}
```

### **Test 7: Refresh Access Token**

```bash
# After 15 minutes, access token expires
# Use refresh token to get new access token
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "{REFRESH_TOKEN}"
  }'
```

**Expected Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // NEW TOKEN
  "refresh_token": null,
  "token_type": "Bearer",
  "expires_in": 900
}
```

### **Test 8: Expired Token**

```bash
# Wait 15+ minutes, then try to use old access token
curl -X GET http://localhost:8080/api/bookings/my \
  -H "Authorization: Bearer {EXPIRED_ACCESS_TOKEN}"
```

**Expected Response (401 Unauthorized):**
```json
{
  "timestamp": "2024-01-15T10:45:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required. Please provide a valid JWT token.",
  "path": "/api/bookings/my"
}
```

### **Test 9: Invalid Token**

```bash
curl -X GET http://localhost:8080/api/bookings/my \
  -H "Authorization: Bearer invalid.token.here"
```

**Expected Response (401 Unauthorized)**

### **Test 10: Validation Errors**

```bash
# Register with invalid data
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "not-an-email",
    "password": "123",
    "name": "J"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "validationErrors": [
    {
      "field": "email",
      "message": "Invalid email format"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters"
    },
    {
      "field": "name",
      "message": "Name must be between 2 and 255 characters"
    }
  ]
}
```

---

## Summary

### **What You Have Now:**

✅ **Complete JWT Authentication System**
- User registration with password hashing
- User login with credential verification
- Access token (15 min) + Refresh token (7 days)
- Token refresh mechanism
- Logout endpoint (client-side)

✅ **Complete Authorization System**
- 3 roles: USER, INSTRUCTOR, ADMIN
- 29 fine-grained permissions
- Hybrid role + permission-based access control
- Method-level security with @PreAuthorize
- Resource-based authorization support

✅ **Security Best Practices**
- BCrypt password hashing (10 rounds)
- JWT signed with HMAC-SHA256
- Stateless authentication (no sessions)
- Short-lived access tokens
- Long-lived refresh tokens
- Account status verification on each request
- Generic error messages (prevent email enumeration)

✅ **Production-Ready Infrastructure**
- Comprehensive error handling (401, 403)
- Input validation with Jakarta Validation
- Proper separation of concerns (Controller → Service → Repository)
- Clean architecture
- Extensible for future features

### **What's NOT Implemented Yet (Future Enhancements):**

⚠️ **Refresh Token Rotation** - Issue new refresh token on each refresh
⚠️ **Token Blacklist** - Revoke tokens before expiration (server-side logout)
⚠️ **Rate Limiting** - Prevent brute force attacks
⚠️ **Account Lockout** - Lock account after N failed login attempts
⚠️ **Email Verification** - Verify email before allowing certain actions
⚠️ **Password Reset** - Forgot password flow
⚠️ **Two-Factor Authentication (2FA)** - Optional second factor
⚠️ **Audit Logging** - Track authentication events
⚠️ **IP Whitelisting** - Restrict access by IP (optional)
⚠️ **Device Tracking** - Track devices per user

### **You're Ready To:**

✅ Build your application endpoints (programs, bookings, events, etc.)
✅ Protect endpoints with `@PreAuthorize` annotations
✅ Access authenticated user in controllers
✅ Test authentication and authorization flows
✅ Deploy to production (after changing JWT secret!)

---

**End of Explanation** 🎉
