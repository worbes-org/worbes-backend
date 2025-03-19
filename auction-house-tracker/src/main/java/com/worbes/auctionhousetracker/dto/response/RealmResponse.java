package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class RealmResponse {

    private Long id;
    private Map<String, String> name;
    private String connectedRealmHref;
    private String slug;

    @JsonProperty("connected_realm")
    private void unpackNestedConnectedRealm(Map<String, String> connectedRealm) {
        this.connectedRealmHref = connectedRealm.get("href");
    }
}
