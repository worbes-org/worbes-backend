package com.worbes.adapter.blizzard.data.realm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RealmIndexResponse(
        @NotNull @JsonIgnoreProperties("realms") List<RealmResponse> realms
) {
}
