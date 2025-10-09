package com.worbes.adapter.blizzard.data.realm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RealmResponse(
        Long id,
        Map<String, String> name,
        String connectedRealmHref,
        String slug,
        boolean isTournament
) {
    @JsonCreator
    public RealmResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("name") Map<String, String> name,
            @JsonProperty("connected_realm") Map<String, String> connectedRealm,
            @JsonProperty("slug") String slug,
            @JsonProperty("is_tournament") boolean isTournament
    ) {
        this(
                id,
                name,
                Optional.ofNullable(connectedRealm).map(m -> m.get("href")).orElse(null),
                slug,
                isTournament
        );
    }
}
