package com.worbes.infra.rest.blizzard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    /**
     * The access token used on future requests to the API.
     */
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    /**
     * Seconds from when received that the token will expire.
     */
    @JsonProperty("expires_in")
    private Long expiresIn;
}
