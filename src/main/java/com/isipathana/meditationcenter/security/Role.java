package com.isipathana.meditationcenter.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

/**
 * User roles in the meditation center system.
 * Each role has a set of permissions that define what actions the user can perform.
 */
public enum Role implements GrantedAuthority {
    /**
     * Regular user - can view programs, create bookings, manage own profile
     */
    USER(Set.of(
            Permission.VIEW_PROGRAMS,
            Permission.CREATE_BOOKING,
            Permission.VIEW_OWN_BOOKINGS,
            Permission.CANCEL_OWN_BOOKING,
            Permission.UPDATE_OWN_PROFILE,
            Permission.VIEW_EVENTS,
            Permission.REGISTER_FOR_EVENT
    )),

    /**
     * Instructor - can manage assigned programs and view student information
     */
    INSTRUCTOR(Set.of(
            Permission.VIEW_PROGRAMS,
            Permission.VIEW_ASSIGNED_PROGRAMS,
            Permission.MARK_ATTENDANCE,
            Permission.VIEW_STUDENTS,
            Permission.UPDATE_OWN_PROFILE
    )),

    /**
     * Administrator - full access to all system features
     */
    ADMIN(Set.of(
            Permission.VIEW_PROGRAMS,
            Permission.CREATE_PROGRAM,
            Permission.UPDATE_PROGRAM,
            Permission.DELETE_PROGRAM,
            Permission.VIEW_ALL_BOOKINGS,
            Permission.CANCEL_ANY_BOOKING,
            Permission.VIEW_USERS,
            Permission.CREATE_USER,
            Permission.UPDATE_USER,
            Permission.DELETE_USER,
            Permission.ASSIGN_INSTRUCTOR,
            Permission.VIEW_EVENTS,
            Permission.CREATE_EVENT,
            Permission.UPDATE_EVENT,
            Permission.DELETE_EVENT,
            Permission.VIEW_DONATIONS,
            Permission.MANAGE_PRICING,
            Permission.VIEW_REPORTS,
            Permission.EXPORT_DATA
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Get all permissions associated with this role.
     *
     * @return Set of permissions
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Check if this role has a specific permission.
     *
     * @param permission The permission to check
     * @return true if the role has this permission
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * Returns the authority string for Spring Security.
     * Format: "ROLE_{name}" (e.g., "ROLE_ADMIN")
     *
     * @return Authority string
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
