package com.worbes.adapter.blizzard.data.shared;

import com.worbes.domain.shared.RegionType;

public class BlizzardApiUrlBuilder {
    private static final String BASE_URL_FORMAT = "https://%s.api.blizzard.com";

    private RegionType region;
    private String path;

    private BlizzardApiUrlBuilder() {
    }

    public static BlizzardApiUrlBuilder builder() {
        return new BlizzardApiUrlBuilder();
    }

    public BlizzardApiUrlBuilder region(RegionType region) {
        this.region = region;
        return this;
    }

    public BlizzardApiUrlBuilder path(String path) {
        this.path = path;
        return this;
    }

    public String build() {
        if (region == null || path == null) {
            throw new IllegalStateException("region과 path는 필수입니다.");
        }
        return String.format(BASE_URL_FORMAT, region.getValue()) + path;
    }
}
