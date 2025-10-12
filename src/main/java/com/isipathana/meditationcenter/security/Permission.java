package com.isipathana.meditationcenter.security;

import org.springframework.security.core.GrantedAuthority;

/**
 * Fine-grained permissions in the meditation center system.
 * Used in conjunction with roles for hybrid authorization.
 * <p>
 * Usage in controllers:
 * - @PreAuthorize("hasAuthority('VIEW_PROGRAMS')")
 * - @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_REPORTS')")
 */
public enum Permission implements GrantedAuthority {
    // Program permissions
    VIEW_PROGRAMS,
    CREATE_PROGRAM,
    UPDATE_PROGRAM,
    DELETE_PROGRAM,

    // Booking permissions
    VIEW_OWN_BOOKINGS,
    VIEW_ALL_BOOKINGS,
    CREATE_BOOKING,
    CANCEL_OWN_BOOKING,
    CANCEL_ANY_BOOKING,

    // User management permissions
    VIEW_USERS,
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,
    UPDATE_OWN_PROFILE,

    // Event permissions
    VIEW_EVENTS,
    CREATE_EVENT,
    UPDATE_EVENT,
    DELETE_EVENT,
    REGISTER_FOR_EVENT,

    // Donation permissions
    VIEW_DONATIONS,

    // Pricing permissions
    MANAGE_PRICING,

    // Reporting permissions
    VIEW_REPORTS,
    EXPORT_DATA;

    /**
     * Returns the authority string for Spring Security.
     * Format: Permission name as-is (e.g., "VIEW_PROGRAMS")
     *
     * @return Authority string
     */
    @Override
    public String getAuthority() {
        return this.name();
    }
}
