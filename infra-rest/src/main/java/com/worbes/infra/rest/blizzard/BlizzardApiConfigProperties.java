package com.worbes.infra.rest.blizzard;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blizzard.api")
@Getter
class BlizzardApiConfigProperties {

    @NotBlank
    private String tokenUrl;

    @NotBlank
    private String tokenBody;

    @NotBlank
    private String tokenKey;

    @NotBlank
    private String id;

    @NotBlank
    private String secret;
}
