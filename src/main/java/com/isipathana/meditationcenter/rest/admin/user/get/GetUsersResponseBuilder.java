package com.isipathana.meditationcenter.rest.admin.user.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.user.User;

import java.util.stream.Stream;

/**
 * Response builder interface for users list.
 *
 * @author Sathira Basnayake
 */
public interface GetUsersResponseBuilder {

    /**
     * Build paginated response from users stream.
     *
     * @param users Stream of users
     * @param currentOffset Current pagination offset
     * @param totalCount Total count of users matching filter
     * @param limit Results per page
     * @return Paginated response
     */
    OffsetSearchResponse<GetUsersResponse> build(Stream<User> users, Integer currentOffset, Long totalCount, Integer limit);
}
