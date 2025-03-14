package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Translation;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmResponse {

    private Long id;
    private Translation name;
    private String connectedRealmHref;

    @JsonProperty("connected_realm")
    private void unpackNestedConnectedRealm(Map<String, String> connectedRealm) {
        this.connectedRealmHref = connectedRealm.get("href");
    }
}
