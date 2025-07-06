package com.worbes.web.realm.model;

import com.worbes.application.realm.model.Realm;

import java.util.Map;

public record GetRealmResponse(
        Map<String, String> name,
        Long id,
        Long connectedRealmId
) {
    public GetRealmResponse(Realm realm) {
        this(realm.getName(), realm.getId(), realm.getConnectedRealmId());
    }
}
