package com.worbes.adapter.blizzard.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BlizzardAccessTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Long expiresIn
) {
}
