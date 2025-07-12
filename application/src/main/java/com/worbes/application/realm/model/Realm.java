package com.worbes.application.realm.model;

import com.worbes.application.common.model.LocalizedName;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
public class Realm {

    private final Long id;
    private final Long connectedRealmId;
    private final RegionType region;
    private final LocalizedName name;
    private final String slug;

    @Builder
    public Realm(Long id, Long connectedRealmId, RegionType region, Map<String, String> name, String slug) {
        this.id = id;
        this.connectedRealmId = connectedRealmId;
        this.region = region;
        this.name = LocalizedName.fromRaw(name);
        this.slug = slug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Realm realm = (Realm) o;
        return Objects.equals(id, realm.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return slug;
    }

    public Map<String, String> getName() {
        return name.asRaw();
    }
}
