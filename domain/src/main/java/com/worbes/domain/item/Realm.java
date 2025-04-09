package com.worbes.domain.item;

import com.worbes.domain.shared.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class Realm {

    private final Long id;
    private final Long connectedRealmId;
    private final RegionType region;
    private final Map<String, String> name;
    private final String slug;

    public static Realm create(Long id,
                               Long connectedRealmId,
                               RegionType region,
                               Map<String, String> name,
                               String slug) {
        return Realm.builder()
                .id(id)
                .connectedRealmId(connectedRealmId)
                .region(region)
                .name(name)
                .slug(slug)
                .build();
    }

    @Override
    public String toString() {
        return slug;
    }
}
