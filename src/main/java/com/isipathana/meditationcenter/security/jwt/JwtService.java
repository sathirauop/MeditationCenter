package com.isipathana.meditationcenter.security.jwt;

import com.isipathana.meditationcenter.config.JwtProperties;
import com.isipathana.meditationcenter.security.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating and validating JWT tokens.
 * Uses HS256 algorithm for signing tokens.
 * <p>
 * Token Types:
 * - Access Token: Short-lived (15 min), contains user info + role
 * - Refresh Token: Long-lived (7 days), used to get new access tokens
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Generate an access token for a user.
     * Contains: userId, email, role in claims.
     * Expiration: Configured in jwt.access-token-expiration (default 15 minutes).
     *
     * @param userId User ID
     * @param email  User email
     * @param role   User role
     * @return JWT access token string
     */
    public String generateAccessToken(Long userId, String email, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role.name());
        claims.put("type", "ACCESS");

        return createToken(claims, email, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * Generate a refresh token for a user.
     * Contains: userId, type only (minimal claims for security).
     * Expiration: Configured in jwt.refresh-token-expiration (default 7 days).
     *
     * @param userId User ID
     * @param email  User email
     * @return JWT refresh token string
     */
    public String generateRefreshToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "REFRESH");

        return createToken(claims, email, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * Validate a JWT token.
     * Checks:
     * - Signature is valid
     * - Token is not expired
     * - Issuer matches
     *
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract user ID from token.
     *
     * @param token JWT token string
     * @return User ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract email from token.
     *
     * @param token JWT token string
     * @return User email
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract role from token.
     *
     * @param token JWT token string
     * @return User role
     */
    public Role extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String roleName = claims.get("role", String.class);
        return Role.valueOf(roleName);
    }

    /**
     * Extract token type (ACCESS or REFRESH).
     *
     * @param token JWT token string
     * @return Token type
     */
    public String extractTokenType(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("type", String.class);
    }

    /**
     * Check if token is expired.
     *
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extract all claims from token.
     *
     * @param token JWT token string
     * @return Claims object
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Create a JWT token with given claims.
     *
     * @param claims           Custom claims to include
     * @param subject          Subject (usually email)
     * @param expirationMillis Expiration time in milliseconds
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject, Long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get the signing key for JWT tokens.
     * Uses HMAC-SHA256 algorithm.
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
