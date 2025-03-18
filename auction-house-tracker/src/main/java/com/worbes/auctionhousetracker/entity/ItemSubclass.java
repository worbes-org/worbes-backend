package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "item_subclass",
        uniqueConstraints = @UniqueConstraint(name = "uq_item_class_subclass", columnNames = {"item_class_id", "subclass_id"})
)
public class ItemSubclass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키 (자동 증가)

    @Column(name = "subclass_id", nullable = false)
    private Long subclassId;  // Blizzard API에서 제공하는 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_class_id", nullable = false)
    private ItemClass itemClass;  // FK (ItemClass와 연관 관계)

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> displayName;  // 다국어 지원

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> verboseName;  // 추가 설명 (NULL 허용)

    public static ItemSubclass create(ItemSubclassResponse response, ItemClass itemClass) {
        return ItemSubclass.builder()
                .subclassId(response.getId())
                .itemClass(itemClass)
                .displayName(response.getDisplayName())
                .verboseName(response.getVerboseName())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemSubclass that = (ItemSubclass) o;
        return Objects.equals(subclassId, that.subclassId) && Objects.equals(itemClass, that.itemClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subclassId, itemClass);
    }
}

