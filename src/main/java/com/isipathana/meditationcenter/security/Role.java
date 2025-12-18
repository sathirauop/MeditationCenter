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
            Permission.REGISTER_FOR_EVENT,
            Permission.VIEW_BOOKS,
            Permission.DOWNLOAD_BOOK
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
            Permission.VIEW_EVENTS,
            Permission.CREATE_EVENT,
            Permission.UPDATE_EVENT,
            Permission.DELETE_EVENT,
            Permission.VIEW_DONATIONS,
            Permission.MANAGE_PRICING,
            Permission.VIEW_REPORTS,
            Permission.EXPORT_DATA,
            Permission.VIEW_ACTIVITIES,
            Permission.CREATE_ACTIVITY,
            Permission.UPDATE_ACTIVITY,
            Permission.DELETE_ACTIVITY,
            Permission.VIEW_TEMPLATES,
            Permission.CREATE_TEMPLATE,
            Permission.UPDATE_TEMPLATE,
            Permission.DELETE_TEMPLATE,
            Permission.ACTIVATE_TEMPLATE,
            Permission.VIEW_BOOKS,
            Permission.CREATE_BOOK,
            Permission.UPDATE_BOOK,
            Permission.DELETE_BOOK,
            Permission.DOWNLOAD_BOOK,
            // Blog permissions (ADMIN only)
            Permission.VIEW_ALL_BLOG_POSTS,
            Permission.CREATE_BLOG_POST,
            Permission.UPDATE_BLOG_POST,
            Permission.DELETE_BLOG_POST,
            Permission.PUBLISH_BLOG_POST,
            Permission.MANAGE_BLOG_TAGS
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
