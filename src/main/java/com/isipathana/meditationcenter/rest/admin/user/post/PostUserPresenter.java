package com.isipathana.meditationcenter.rest.admin.user.post;

import com.isipathana.meditationcenter.records.user.User;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming created user to response DTO.
 *
 * @author Sathira Basnayake
 */
@Component
public class PostUserPresenter implements PostUserResponseBuilder {

    @Override
    public PostUserResponse build(User user) {
        return PostUserResponse.builder()
                .userId(user.userId())
                .email(user.email())
                .name(user.name())
                .mobileNumber(user.mobileNumber())
                .role(user.role().name())
                .isActive(user.isActive())
                .emailVerified(user.emailVerified())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }
}
