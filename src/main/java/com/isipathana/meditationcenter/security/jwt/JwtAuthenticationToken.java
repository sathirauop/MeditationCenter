package com.isipathana.meditationcenter.security.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authentication token for JWT-based authentication.
 * <p>
 * Two states:
 * 1. Unauthenticated: Contains only JWT string (before validation)
 * 2. Authenticated: Contains JWT + principal + authorities (after validation)
 */
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwt;
    private final Object principal;

    /**
     * Constructor for unauthenticated token (before validation).
     * Used by JwtAuthenticationFilter.
     *
     * @param jwt The JWT token string
     */
    public JwtAuthenticationToken(String jwt) {
        super(null);
        this.jwt = jwt;
        this.principal = null;
        setAuthenticated(false);
    }

    /**
     * Constructor for authenticated token (after validation).
     * Used by JwtAuthenticationProvider.
     *
     * @param jwt         The JWT token string
     * @param principal   The authenticated user (MeditationCenterUser)
     * @param authorities The user's authorities (roles + permissions)
     */
    public JwtAuthenticationToken(
            String jwt,
            Object principal,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
