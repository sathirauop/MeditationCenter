package com.isipathana.meditationcenter.rest.admin.user.get;

import com.isipathana.meditationcenter.models.response.OffsetSearchResponse;
import com.isipathana.meditationcenter.records.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

/**
 * Presenter for transforming users to response DTOs.
 *
 * @author Sathira Basnayake
 */
@Component
@RequiredArgsConstructor
public class GetUsersPresenter implements GetUsersResponseBuilder {

    private final OffsetSearchResponse.Factory responseFactory;

    @Override
    public OffsetSearchResponse<GetUsersResponse> build(Stream<User> users, Integer currentOffset, Long totalCount, Integer limit) {
        List<GetUsersResponse> responseList = users
                .map(this::mapUserToResponse)
                .toList();

        // Calculate max offset
        Integer maxOffset = calculateMaxOffset(totalCount, limit);

        return responseFactory.create(responseList, currentOffset, maxOffset);
    }

    /**
     * Map User domain object to GetUsersResponse DTO.
     */
    private GetUsersResponse mapUserToResponse(User user) {
        return GetUsersResponse.builder()
                .userId(user.userId())
                .email(user.email())
                .name(user.name())
                .mobileNumber(user.mobileNumber())
                .role(user.role().name())
                .isActive(user.isActive())
                .emailVerified(user.emailVerified())
                .avatarUrl(null) // TODO: Generate presigned URL when R2 avatar support is added
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }

    /**
     * Calculate maximum offset for pagination.
     */
    private Integer calculateMaxOffset(Long totalCount, Integer limit) {
        if (totalCount == null || totalCount == 0 || limit == null || limit == 0) {
            return 0;
        }
        return (int) Math.max(0, totalCount - limit);
    }
}
