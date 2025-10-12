package com.isipathana.meditationcenter.rest.auth.refresh;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for token refresh.
 */
public record PostRefreshRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
