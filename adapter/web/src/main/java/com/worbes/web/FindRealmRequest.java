package com.worbes.web;

import com.worbes.application.realm.model.RegionType;

public record FindRealmRequest(
        RegionType region
) {
}
