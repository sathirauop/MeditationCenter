package com.isipathana.meditationcenter.rest.auth.login;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for user login.
 */
public record PostLoginResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn
) {
    public PostLoginResponse(String accessToken, String refreshToken, Long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
