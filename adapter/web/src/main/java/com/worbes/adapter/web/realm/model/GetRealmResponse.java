package com.worbes.adapter.web.realm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.application.realm.model.Realm;

import java.util.Map;

public record GetRealmResponse(
        @JsonProperty("name") Map<String, String> name,
        @JsonProperty("id") Long id,
        @JsonProperty("connected_realm_id") Long connectedRealmId
) {
    public GetRealmResponse(Realm realm) {
        this(realm.getName(), realm.getId(), realm.getConnectedRealmId());
    }
}
