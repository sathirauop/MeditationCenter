# bow-user Security Architecture Analysis

## Overview

The bow-user project uses **AWS Cognito for authentication** with a **Bearer Token (JWT)** approach. The architecture is stateless and designed for RESTful APIs.

---

## Authentication Flow

```
Client Request with Bearer Token
       ↓
BearerTokenAuthenticationFilter (extracts token from Authorization header)
       ↓
AuthenticationManager
       ↓
AwsCognitoAuthenticationProvider (validates token with AWS Cognito)
       ↓
AuthClient calls external Cognito service
       ↓
Returns AwsCognitoUser with username + authorities
       ↓
SecurityContext populated with authenticated user
       ↓
Request proceeds to controller
```

---

## Key Components

### **1. BearerTokenAuthenticationFilter**

**Location:** `api/core/auth/filter/BearerTokenAuthenticationFilter.java`

**Purpose:** Intercepts HTTP requests and extracts Bearer token

**How it works:**
```java
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                      ↑
                 Extracts this token
```

**Key Features:**
- Extends `OncePerRequestFilter` (runs once per request)
- Extracts token from `Authorization` header
- Creates `BearerTokenAuthenticationToken`
- Delegates to `AuthenticationManager` for validation
- Populates `SecurityContext` on success
- Handles exceptions gracefully
- `ignoreFailure` flag: Can continue without auth (for public endpoints)

**Code Flow:**
```
1. resolveAuthorizationToken() → Extract "Bearer XXX"
2. Create BearerTokenAuthenticationToken(token)
3. authenticationManager.authenticate(token)
4. SecurityContextHolder.setContext(authenticatedContext)
5. Continue filter chain
```

---

### **2. AuthenticationProvider (Abstract + Concrete)**

#### **BearerTokenAuthenticationProvider (Abstract)**

**Location:** `api/core/auth/provider/BearerTokenAuthenticationProvider.java`

**Purpose:** Base class for all bearer token authentication strategies

**Pattern:** Template Method Pattern

**Key Methods:**
- `doAuthenticate()` → Abstract method (subclass implements)
- `authenticate()` → Template method (handles common logic)
- `supports()` → Returns true for `BearerTokenAuthenticationToken`

**Error Handling:**
- Catches `AuthenticationException` / `ForbiddenException`
- Wraps in `BearerTokenAuthenticationException`
- Provides error codes: `INVALID_TOKEN`, `INVALID_REQUEST`

#### **AwsCognitoAuthenticationProvider (Concrete)**

**Location:** `api/core/auth/provider/AwsCognitoAuthenticationProvider.java`

**Purpose:** Validates JWT with AWS Cognito

**Flow:**
```java
1. Receive BearerTokenAuthenticationToken (contains JWT)
2. Call authClient.getCognitoUser(accessToken)
3. External HTTP call to AWS Cognito service
4. Cognito validates JWT and returns user info
5. Create AwsCognitoUser(username, authorities[])
6. Return AwsCognitoAuthenticationToken
```

**Important:** This delegates authentication to **external AWS Cognito service**

---

### **3. Authentication Tokens**

#### **BearerTokenAuthenticationToken**

**Location:** `api/core/auth/token/BearerTokenAuthenticationToken.java`

**Purpose:** Unauthenticated token (holds raw JWT string)

**Contents:**
- `token: String` → The JWT from Authorization header
- Extends `BaseAuthenticationToken`
- Created by filter **before** authentication

#### **AwsCognitoAuthenticationToken**

**Purpose:** Authenticated token (holds validated user)

**Contents:**
- `AwsCognitoUser` → Username + authorities
- Returned **after** successful authentication
- Stored in SecurityContext

---

### **4. User Principals**

#### **AuthenticatedUser (Base)**

**Location:** `api/core/auth/user/AuthenticatedUser.java`

**Purpose:** Base interface for all authenticated users

**Simple implementation:**
```java
public class AuthenticatedUser implements AuthenticatedPrincipal {
    public String getName() { return ""; }
}
```

#### **AwsCognitoUser (Concrete)**

**Location:** `api/core/auth/user/AwsCognitoUser.java`

**Purpose:** Represents Cognito-authenticated user

**Contains:**
- `username: String` → Cognito username
- `authorities: String[]` → Permissions/roles from Cognito
- `getName()` → Returns "aws.cognito:username"

**Important:** Authorities come from AWS Cognito, not local database

---

### **5. AuthClient (External Service Call)**

**Location:** `core/http/auth/AuthClient.java`

**Purpose:** HTTP client to call external authentication service

**Endpoint:** `GET /cognito` with Bearer token

**Response:** `GetCognitoUserResponse(username, authorities[])`

**This is KEY:** bow-user doesn't validate JWTs locally. It delegates to external service.

---

### **6. Security Configuration**

#### **WebSecurityConfigurer (Abstract Base)**

**Location:** `api/core/security/WebSecurityConfigurer.java`

**Purpose:** Base configuration for Spring Security

**Key Settings:**
```java
- CSRF disabled (stateless API)
- Session management: STATELESS
- Custom AuthenticationEntryPoint (handles 401)
- Custom AccessDeniedHandler (handles 403)
- BearerTokenAuthenticationFilter added before AnonymousAuthenticationFilter
```

**Abstract method:**
- `awsCognitoSecurityFilterChain()` → Subclass defines which endpoints need auth

#### **SecurityConfiguration (Concrete)**

**Location:** `api/configuration/SecurityConfiguration.java`

**Purpose:** Actual security rules for the application

**Configuration:**
```java
@Override
protected SecurityFilterChain awsCognitoSecurityFilterChain(...) {
    // 1. Configure bearer token authentication
    configureBearerTokenAuthentication(httpSecurity, authenticationManager);

    // 2. Define which paths require authentication
    httpSecurity
        .securityMatcher(APP_ROOT_PATH + "/**")  // e.g., "/api/**"
        .authorizeHttpRequests(authorize ->
            authorize.requestMatchers(APP_ROOT_PATH + "/**").authenticated()
        );

    return httpSecurity.build();
}
```

**What this does:**
- All requests to `/api/**` require authentication
- No role-based or permission-based checks here (just authenticated vs not)

---

### **7. Exception Handlers**

#### **AuthenticationEntryPoint**

**Location:** `api/core/security/AuthenticationEntryPoint.java`

**Purpose:** Handles authentication failures (401 Unauthorized)

**Key Features:**
- Implements Spring's `AuthenticationEntryPoint` interface
- Sets `WWW-Authenticate` header with error details
- Returns 401/400/403 based on error type

**Error Codes:**
- `INVALID_REQUEST` → 400 Bad Request
- `INVALID_TOKEN` → 401 Unauthorized
- `INSUFFICIENT_SCOPE` → 403 Forbidden

**WWW-Authenticate Header Format:**
```
WWW-Authenticate: Bearer realm="backoffice", error="invalid_token", error_description="The bearer token is invalid."
```

#### **AccessDeniedHandler**

**Location:** `api/core/security/AccessDeniedHandler.java`

**Purpose:** Handles authorization failures (403 Forbidden)

**Simple implementation:**
- Returns 403 with JSON content type
- Used when authenticated user lacks required permissions

---

### **8. Authorization (Roles/Permissions)**

#### **ApiPermissionAuthority (Enum)**

**Location:** `api/core/security/ApiPermissionAuthority.java`

**Purpose:** Defines available authorities/roles

**Current implementation:**
```java
public enum ApiPermissionAuthority implements GrantedAuthority {
    AWS_COGNITO;  // Only one authority defined

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();  // Returns "ROLE_AWS_COGNITO"
    }
}
```

**Important Observations:**
- Only ONE authority defined: `AWS_COGNITO`
- All authenticated Cognito users get this role
- No fine-grained permissions (USER, ADMIN, INSTRUCTOR, etc.)
- Permissions likely managed in AWS Cognito, not in code

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Application                       │
│            (Sends: Authorization: Bearer JWT)               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              BearerTokenAuthenticationFilter                │
│          (Extracts JWT from Authorization header)           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   AuthenticationManager                     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│           AwsCognitoAuthenticationProvider                  │
│     (Delegates validation to external Cognito service)      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                       AuthClient                            │
│         (HTTP call to: GET /cognito with JWT)               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              External AWS Cognito Service                   │
│        (Validates JWT, returns username + authorities)      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    AwsCognitoUser                           │
│           (username: "john", authorities: ["ADMIN"])        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   SecurityContext                           │
│            (Stores authenticated user for request)          │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Controller                             │
│           (Access user via: Authentication object)          │
└─────────────────────────────────────────────────────────────┘
```

---

## Key Patterns Used

### **1. Chain of Responsibility**
- Filter → AuthenticationManager → Provider → External Service

### **2. Template Method**
- `BearerTokenAuthenticationProvider` defines template
- `AwsCognitoAuthenticationProvider` fills in details

### **3. Strategy Pattern**
- Different authentication providers for different auth methods
- Can add: `JwtAuthenticationProvider`, `OAuth2Provider`, etc.

### **4. Delegation Pattern**
- Actual JWT validation delegated to AWS Cognito
- Not done locally

---

## What bow-user Does Well

### ✅ **Separation of Concerns**
- Filter extracts token
- Provider validates token
- Configuration defines security rules
- Clean separation

### ✅ **Extensible**
- Easy to add new authentication providers
- Abstract base classes for customization

### ✅ **Stateless**
- No session management
- Perfect for REST APIs

### ✅ **Error Handling**
- Proper exception types
- Standard HTTP status codes
- WWW-Authenticate headers

### ✅ **Spring Security Best Practices**
- Extends standard Spring Security components
- Uses SecurityContext properly
- Filter ordering correct

---

## What's Missing / Limitations

### ❌ **No Local JWT Validation**
- All validation delegated to external service
- Extra HTTP call per request (performance overhead)
- Depends on external service availability

### ❌ **No Refresh Token Implementation**
- Only access tokens handled
- No token refresh flow

### ❌ **No Fine-Grained Authorization**
- Only one authority: `AWS_COGNITO`
- No `@PreAuthorize("hasRole('ADMIN')")` usage
- Permissions managed externally in Cognito

### ❌ **No Login/Logout Endpoints**
- bow-user only validates existing tokens
- Login handled by AWS Cognito (external)
- No user registration flow

### ❌ **No Password Management**
- All authentication via external Cognito
- No password reset, email verification, etc.

### ❌ **No Token Blacklist**
- Once token issued, valid until expiry
- Cannot revoke tokens

---

## Differences from Standard JWT Implementation

| Feature | bow-user (AWS Cognito) | Standard JWT |
|---------|------------------------|--------------|
| **Token Validation** | External HTTP call to Cognito | Local validation with secret key |
| **User Storage** | AWS Cognito | Local database |
| **Login Endpoint** | External (AWS Cognito) | Local `/auth/login` |
| **Token Generation** | AWS Cognito | Local JWT library (jjwt, nimbus) |
| **Refresh Tokens** | Not implemented | Typically implemented |
| **Permissions** | Stored in Cognito | Stored in local database |
| **Performance** | HTTP call overhead | Fast (local validation) |
| **Dependencies** | Requires AWS Cognito | No external dependencies |

---

## How Authorities Work

### **In bow-user:**
```
1. User logs in via AWS Cognito (external)
2. Cognito returns JWT with embedded authorities
3. JWT sent to bow-user API
4. bow-user calls Cognito to validate JWT
5. Cognito returns: { username: "john", authorities: ["ADMIN", "USER"] }
6. AwsCognitoUser created with these authorities
7. Spring Security uses authorities for authorization
```

### **Where authorities are defined:**
- **NOT in bow-user code**
- **NOT in bow-user database**
- **IN AWS Cognito** (external service)

### **How to check authorities in controller:**
```java
@GetMapping("/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")  // This would work
public List<User> getAllUsers() {
    // Only users with 'ADMIN' authority from Cognito can access
}
```

**But:** bow-user doesn't use this. All authenticated users can access all endpoints (no @PreAuthorize seen).

---

## Recommendations for MeditationCenter

### **DO NOT Copy bow-user Approach If:**
- ❌ You don't want to use AWS Cognito
- ❌ You want local user management
- ❌ You want full control over authentication
- ❌ You want to avoid external service dependencies
- ❌ You want to minimize latency (no external HTTP calls)

### **Adapt from bow-user:**
- ✅ Overall architecture (Filter → Provider → Token pattern)
- ✅ Security configuration structure
- ✅ Exception handling approach
- ✅ Stateless design

### **Implement Differently:**
- ✅ Local JWT generation and validation (no external calls)
- ✅ Local user storage (PostgreSQL)
- ✅ Login/logout endpoints in your API
- ✅ Refresh token mechanism
- ✅ Fine-grained roles and permissions (USER, ADMIN, INSTRUCTOR)
- ✅ Resource-based authorization (users can only edit their bookings)

---

## MeditationCenter Security Design (Based on bow-user Patterns)

### **Recommended Architecture:**

```
Client sends credentials
       ↓
POST /auth/login (LoginController)
       ↓
LoginUseCase validates credentials
       ↓
Generate JWT with roles/permissions
       ↓
Return JWT + Refresh Token
       ↓
---
Client sends JWT in subsequent requests
       ↓
JwtAuthenticationFilter (extracts JWT)
       ↓
JwtAuthenticationProvider (validates JWT locally with secret key)
       ↓
Creates MeditationCenterUser with roles
       ↓
SecurityContext populated
       ↓
Controller checks: @PreAuthorize("hasRole('ADMIN')")
```

### **Components to Create:**

1. **JwtAuthenticationFilter** (similar to BearerTokenAuthenticationFilter)
   - Extract JWT from Authorization header
   - Create JwtAuthenticationToken

2. **JwtAuthenticationProvider** (similar to AwsCognitoAuthenticationProvider)
   - Validate JWT signature locally (no external call!)
   - Extract user ID, roles, permissions from JWT claims
   - Load full user from database (optional, or trust JWT)
   - Create MeditationCenterUser

3. **MeditationCenterUser** (similar to AwsCognitoUser)
   - userId, email, username
   - roles: [USER, ADMIN, INSTRUCTOR]
   - permissions: [CREATE_BOOKING, DELETE_PROGRAM, etc.]

4. **JwtService** (NEW - not in bow-user)
   - `generateAccessToken(userId, roles, permissions)` → JWT
   - `generateRefreshToken(userId)` → Long-lived token
   - `validateToken(jwt)` → boolean
   - `extractClaims(jwt)` → userId, roles, permissions

5. **AuthenticationController** (NEW - not in bow-user)
   - `POST /auth/login` → Returns JWT + refresh token
   - `POST /auth/refresh` → Returns new JWT
   - `POST /auth/logout` → Invalidates refresh token

6. **SecurityConfiguration** (similar to bow-user)
   - Configure filter chain
   - Define public endpoints (login, register)
   - Require authentication for other endpoints

7. **Authorization with @PreAuthorize**
   ```java
   @PreAuthorize("hasRole('ADMIN')")
   public void deleteProgram(Long id) { }

   @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#userId)")
   public void updateUser(Long userId) { }
   ```

---

## Summary: bow-user Security Architecture

**Authentication:** AWS Cognito (external OAuth2-like service)
**Authorization:** Basic (only checks if authenticated)
**Tokens:** JWT (validated externally)
**User Management:** External (AWS Cognito)
**Roles/Permissions:** Defined in AWS Cognito
**Stateless:** Yes ✅
**Scalable:** Yes ✅ (but with HTTP call overhead)
**Local Control:** No ❌

**Best for:** Enterprise apps using AWS Cognito for centralized authentication

**NOT suitable for:** Projects wanting full control, local user management, or minimal dependencies

---

## Next Steps for MeditationCenter

1. **Decide:** Local JWT vs External Service (Recommend: Local JWT)
2. **Design:** Roles (USER, ADMIN, INSTRUCTOR) + Permissions
3. **Implement:** JWT generation, validation, refresh token logic
4. **Create:** Authentication endpoints (login, register, refresh, logout)
5. **Configure:** Spring Security with local JWT validation
6. **Add:** Authorization checks with @PreAuthorize
7. **Test:** With Postman/REST Client

**Ready to implement when you are!**
