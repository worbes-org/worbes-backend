package com.worbes.adapter.blizzard.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "blizzard.api")
public record BlizzardConfigPropertiesImpl(
        @NotBlank String id,
        @NotBlank String secret,
        @NotBlank String tokenUrl,
        @NotBlank String tokenKey
) implements BlizzardConfigProperties {
}
