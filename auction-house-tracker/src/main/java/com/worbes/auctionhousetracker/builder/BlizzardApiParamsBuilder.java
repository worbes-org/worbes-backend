package com.worbes.auctionhousetracker.builder;

import com.worbes.auctionhousetracker.entity.enums.NamespaceType;
import com.worbes.auctionhousetracker.entity.enums.Region;

import java.util.HashMap;
import java.util.Map;

public class BlizzardApiParamsBuilder {

    private static final String NAMESPACE = "namespace";
    private final Map<String, String> params = new HashMap<>();
    private final Region region;

    private BlizzardApiParamsBuilder(Region region) {
        if (region == null) {
            throw new IllegalArgumentException("Region은 필수값입니다.");
        }
        this.region = region;
    }

    public static BlizzardApiParamsBuilder builder(Region region) {
        return new BlizzardApiParamsBuilder(region);
    }

    public BlizzardApiParamsBuilder namespace(NamespaceType type) {
        params.put(NAMESPACE, type.format(region));
        return this;
    }

    public Map<String, String> build() {
        checkNamespaceIsSet();
        return new HashMap<>(params);
    }

    private void checkNamespaceIsSet() {
        if (!params.containsKey(NAMESPACE)) {
            throw new IllegalStateException("Namespace가 설정되지 않았습니다. 먼저 namespace()를 호출하세요.");
        }
    }
}
