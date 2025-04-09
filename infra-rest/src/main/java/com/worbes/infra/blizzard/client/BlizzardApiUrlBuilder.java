package com.worbes.infra.blizzard.client;


import com.worbes.domain.shared.RegionType;

public class BlizzardApiUrlBuilder {

    private static final String BASE_URL = "https://%s.api.blizzard.com";

    private final RegionType region;
    private String path;

    private BlizzardApiUrlBuilder(RegionType region) {
        if (region == null) {
            throw new IllegalArgumentException("Region은 필수값입니다.");
        }
        this.region = region;
    }

    public static BlizzardApiUrlBuilder builder(RegionType region) {
        return new BlizzardApiUrlBuilder(region);
    }

    public BlizzardApiUrlBuilder auctions(Long realmId) {
        this.path = String.format("/data/wow/connected-realm/%s/auctions", realmId);
        return this;
    }

    public BlizzardApiUrlBuilder commodities() {
        this.path = "/data/wow/auctions/commodities";
        return this;
    }

    public BlizzardApiUrlBuilder itemClassIndex() {
        this.path = "/data/wow/item-class/index";
        return this;
    }

    public BlizzardApiUrlBuilder itemClass(Long id) {
        this.path = String.format("/data/wow/item-class/%s", id);
        return this;
    }

    public BlizzardApiUrlBuilder itemSubclass(Long itemClassId, Long subclassId) {
        this.path = String.format("/data/wow/item-class/%s/item-subclass/%s", itemClassId, subclassId);
        return this;
    }

    public BlizzardApiUrlBuilder item(Long id) {
        this.path = String.format("/data/wow/item/%s", id);
        return this;
    }

    public BlizzardApiUrlBuilder media(Long id) {
        this.path = String.format("/data/wow/media/item/%s", id);
        return this;
    }

    public BlizzardApiUrlBuilder realmIndex() {
        this.path = "/data/wow/realm/index";
        return this;
    }

    public BlizzardApiUrlBuilder realm(String slug) {
        this.path = String.format("/data/wow/realm/%s", slug);
        return this;
    }

    public String build() {
        checkPathIsSet(); // path 검증
        return String.format(BASE_URL, region.getValue()).concat(path);
    }

    private void checkPathIsSet() {
        if (path == null) {
            throw new IllegalStateException("경로(path)가 설정되지 않았습니다. commodities() 등을 호출하세요.");
        }
    }
}
