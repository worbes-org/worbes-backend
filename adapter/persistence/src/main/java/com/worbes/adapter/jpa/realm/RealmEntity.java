package com.worbes.adapter.jpa.realm;

import com.worbes.adapter.jpa.common.BaseEntity;
import com.worbes.application.realm.model.RegionType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.Objects;

@Table(
        name = "realm",
        uniqueConstraints = @UniqueConstraint(name = "uq_region_slug", columnNames = {"region", "slug"})
)
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RealmEntity extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false, name = "connected_realm_id")
    private Long connectedRealmId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private RegionType region;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @Column(length = 50, nullable = false)
    private String slug;

    @Builder
    private RealmEntity(
            Long id,
            Long connectedRealmId,
            RegionType region,
            Map<String, String> name,
            String slug
    ) {
        this.id = id;
        this.connectedRealmId = connectedRealmId;
        this.region = region;
        this.name = name;
        this.slug = slug;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RealmEntity that = (RealmEntity) o;
        return Objects.equals(id, that.id) && region == that.region;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region);
    }
}
