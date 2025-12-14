package com.isipathana.meditationcenter.rest.admin.user.getById;

import com.isipathana.meditationcenter.records.user.User;
import com.isipathana.meditationcenter.records.user.UserStatistics;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming user with statistics to response DTO.
 *
 * @author Sathira Basnayake
 */
@Component
public class GetUserByIdPresenter implements GetUserByIdResponseBuilder {

    @Override
    public GetUserByIdResponse build(User user, UserStatistics statistics) {
        return GetUserByIdResponse.builder()
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
                .statistics(statistics)
                .build();
    }
}
