package com.worbes.web.realm.model;

import com.worbes.application.realm.model.RegionType;
import jakarta.validation.constraints.NotNull;

public record FindRealmRequest(
        @NotNull(message = "region must not be null") RegionType region
) {
}
