package com.isipathana.meditationcenter.rest.admin.user.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

/**
 * UseCase for getting paginated list of users with filtering.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetUsersUseCase {

    private final GetUsersDataAccess repository;
    private final GetUsersResponseBuilder presenter;

    /**
     * Execute use case to get users with pagination and filtering.
     *
     * @param request Request parameters (limit, offset, filters)
     * @return Paginated response with users
     */
    @Transactional(readOnly = true)
    public OffsetSearchResponse<GetUsersResponse> execute(GetUsersRequest request) {
        // Apply defaults to request
        GetUsersRequest normalizedRequest = request.withDefaults();

        log.info("Fetching users: limit={}, offset={}, role={}, isActive={}, search={}",
                normalizedRequest.limit(),
                normalizedRequest.offset(),
                normalizedRequest.role(),
                normalizedRequest.isActive(),
                normalizedRequest.search());

        // Get total count for pagination
        Long totalCount = repository.countUsers(
                normalizedRequest.role(),
                normalizedRequest.isActive(),
                normalizedRequest.search()
        );

        log.debug("Total users matching criteria: {}", totalCount);

        // Fetch users with filters
        Stream<User> users = repository.findUsers(
                normalizedRequest.limit(),
                normalizedRequest.offset(),
                normalizedRequest.role(),
                normalizedRequest.isActive(),
                normalizedRequest.search()
        );

        // Build response
        return presenter.build(
                users,
                normalizedRequest.offset(),
                totalCount,
                normalizedRequest.limit()
        );
    }
}
