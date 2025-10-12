package com.isipathana.meditationcenter.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an authenticated user in the meditation center system.
 * Implements Spring Security's UserDetails interface.
 * <p>
 * This class is stored in SecurityContext after successful authentication
 * and can be retrieved in controllers via:
 * - @AuthenticationPrincipal MeditationCenterUser user
 * - Authentication.getPrincipal()
 */
@Getter
@Builder
public class MeditationCenterUser implements UserDetails {

    /**
     * User ID from database
     */
    private final Long userId;

    /**
     * User's email (used as username)
     */
    private final String email;

    /**
     * User's display name
     */
    private final String name;

    /**
     * User's role (USER, ADMIN, INSTRUCTOR)
     */
    private final Role role;

    /**
     * Whether the user account is active
     */
    private final Boolean isActive;

    /**
     * Whether the user's email is verified
     */
    private final Boolean emailVerified;

    /**
     * Get all authorities (role + permissions).
     * Returns: ROLE_ADMIN, VIEW_PROGRAMS, CREATE_PROGRAM, etc.
     *
     * @return Collection of GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add role (e.g., ROLE_ADMIN)
        authorities.add(role);

        // Add all permissions from role (e.g., VIEW_PROGRAMS, CREATE_PROGRAM)
        authorities.addAll(role.getPermissions());

        return authorities;
    }

    /**
     * Returns null as we don't store passwords in this principal.
     * Password is only used during authentication, not stored in SecurityContext.
     *
     * @return null
     */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * Returns email as the username.
     *
     * @return User's email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Account is not expired (we don't implement account expiration).
     *
     * @return true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Account is not locked (we don't implement account locking yet).
     *
     * @return true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credentials never expire (JWT handles expiration).
     *
     * @return true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Account is enabled if isActive is true.
     *
     * @return true if account is active
     */
    @Override
    public boolean isEnabled() {
        return isActive != null && isActive;
    }

    /**
     * Check if user has a specific role.
     *
     * @param checkRole Role to check
     * @return true if user has this role
     */
    public boolean hasRole(Role checkRole) {
        return this.role == checkRole;
    }

    /**
     * Check if user has a specific permission.
     *
     * @param permission Permission to check
     * @return true if user has this permission
     */
    public boolean hasPermission(Permission permission) {
        return role.hasPermission(permission);
    }
}
