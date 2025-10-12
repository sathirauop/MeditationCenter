package com.isipathana.meditationcenter.rest.auth.refresh;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for token refresh.
 * Note: Does not include refresh token, only new access token.
 */
public record PostRefreshResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn
) {
    public PostRefreshResponse(String accessToken, Long expiresIn) {
        this(accessToken, "Bearer", expiresIn);
    }
}
