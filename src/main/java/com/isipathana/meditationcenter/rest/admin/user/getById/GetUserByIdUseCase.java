package com.isipathana.meditationcenter.rest.admin.user.getById;

import com.isipathana.meditationcenter.exception.ResourceNotFoundException;
import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for getting user details by ID with statistics.
 *
 * @author Sathira Basnayake
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final GetUserByIdDataAccess repository;
    private final GetUserByIdResponseBuilder presenter;

    /**
     * Execute use case to get user by ID with statistics.
     *
     * @param userId User ID
     * @return User details with statistics
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public GetUserByIdResponse execute(Long userId) {
        log.info("Fetching user details for userId: {}", userId);

        // Find user
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        log.debug("User found: {}", user.email());

        // Get user statistics
        UserStatistics statistics = repository.getUserStatistics(userId);

        log.debug("User statistics: totalBookings={}, activeBookings={}, totalDonations={}, eventRegistrations={}",
                statistics.totalBookings(),
                statistics.activeBookings(),
                statistics.totalDonations(),
                statistics.eventRegistrations());

        // Build response
        return presenter.build(user, statistics);
    }
}
