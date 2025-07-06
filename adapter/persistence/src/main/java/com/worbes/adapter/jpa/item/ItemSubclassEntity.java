package com.worbes.adapter.jpa.item;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.Objects;

@Table(name = "item_subclass")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class
ItemSubclassEntity {

    @EmbeddedId
    private ItemSubclassId id;

    @MapsId("itemClassId") // 복합키의 itemClassId를 외래키로 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_class_id", nullable = false)
    private ItemClassEntity itemClass;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false, name = "display_name")
    private Map<String, String> displayName;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", name = "vervose_name")
    private Map<String, String> verboseName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemSubclassEntity that = (ItemSubclassEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
