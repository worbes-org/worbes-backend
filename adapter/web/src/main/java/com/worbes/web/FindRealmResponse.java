package com.worbes.web;

import com.worbes.application.realm.model.Realm;

import java.util.Map;

public record FindRealmResponse(
        Map<String, String> name,
        Long id,
        Long connectedRealmId
) {
    public FindRealmResponse(Realm realm) {
        this(realm.getName(), realm.getId(), realm.getConnectedRealmId());
    }
}
