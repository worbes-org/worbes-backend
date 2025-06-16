package com.worbes.adapter.blizzard.data.realm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmResponse {

    private Long id;
    private Map<String, String> name;
    private String connectedRealmHref;
    private String slug;

    @JsonProperty("is_tournament")
    private boolean isTournament;

    @JsonProperty("connected_realm")
    private void unpackNestedConnectedRealm(Map<String, String> connectedRealm) {
        this.connectedRealmHref = connectedRealm.get("href");
    }
}
