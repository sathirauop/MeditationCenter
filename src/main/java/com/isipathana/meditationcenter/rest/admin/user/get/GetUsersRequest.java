package com.isipathana.meditationcenter.rest.admin.user.get;

import com.isipathana.meditationcenter.records.user.UserRole;

/**
 * Request parameters for getting paginated list of users.
 *
 * @author Sathira Basnayake
 */
public record GetUsersRequest(
        Integer limit,
        Integer offset,
        UserRole role,
        Boolean isActive,
        String search
) {
    /**
     * Default values for pagination.
     */
    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;
    public static final int DEFAULT_OFFSET = 0;

    /**
     * Create request with defaults applied.
     */
    public GetUsersRequest withDefaults() {
        return new GetUsersRequest(
                limit != null ? Math.min(limit, MAX_LIMIT) : DEFAULT_LIMIT,
                offset != null ? offset : DEFAULT_OFFSET,
                role,
                isActive,
                search
        );
    }
}
