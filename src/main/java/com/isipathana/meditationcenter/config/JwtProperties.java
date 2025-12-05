package com.isipathana.meditationcenter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties loaded from application.properties.
 * <p>
 * Configuration format in application.properties:
 * <pre>
 * jwt.secret=your-256-bit-secret-key-change-this-in-production
 * jwt.access-token-expiration=900000
 * jwt.refresh-token-expiration=604800000
 * jwt.issuer=meditation-center
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key for signing JWTs.
     * IMPORTANT: Must be at least 256 bits (32 characters) for HS256 algorithm.
     * Change this in production and store securely (environment variable or secrets manager).
     */
    private String secret;

    /**
     * Access token expiration time in milliseconds.
     * Default: 900000ms = 15 minutes
     */
    private Long accessTokenExpiration = 900000L; // 15 minutes

    /**
     * Refresh token expiration time in milliseconds.
     * Default: 604800000ms = 7 days
     */
    private Long refreshTokenExpiration = 604800000L; // 7 days

    /**
     * JWT issuer claim (identifies who issued the token).
     * Default: "meditation-center"
     */
    private String issuer = "meditation-center";
}
