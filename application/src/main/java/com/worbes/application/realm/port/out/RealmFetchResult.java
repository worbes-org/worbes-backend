package com.worbes.application.realm.port.out;

import com.worbes.application.realm.model.RegionType;

import java.util.Map;

public record RealmFetchResult(
        Long id,
        Map<String, String> name,
        String connectedRealmHref,
        String slug,
        RegionType region
) {
}
