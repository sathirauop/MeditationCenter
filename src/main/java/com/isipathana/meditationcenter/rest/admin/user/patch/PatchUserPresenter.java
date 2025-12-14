package com.isipathana.meditationcenter.rest.admin.user.patch;

import com.isipathana.meditationcenter.records.user.User;
import org.springframework.stereotype.Component;

/**
 * Presenter for transforming updated user to response DTO.
 *
 * @author Sathira Basnayake
 */
@Component
public class PatchUserPresenter implements PatchUserResponseBuilder {

    @Override
    public PatchUserResponse build(User user) {
        return PatchUserResponse.builder()
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
