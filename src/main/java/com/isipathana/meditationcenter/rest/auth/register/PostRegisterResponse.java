package com.isipathana.meditationcenter.rest.auth.register;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for user registration.
 */
public record PostRegisterResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn
) {
    public PostRegisterResponse(String accessToken, String refreshToken, Long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
