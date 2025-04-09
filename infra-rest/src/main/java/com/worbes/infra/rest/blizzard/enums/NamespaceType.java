package com.worbes.infra.rest.blizzard.enums;

import com.worbes.domain.shared.RegionType;

public enum NamespaceType {
    DYNAMIC("dynamic-%s"),
    STATIC("static-%s");

    private final String format;

    NamespaceType(String format) {
        this.format = format;
    }

    public String format(RegionType region) {
        return String.format(this.format, region.getValue());
    }
}
