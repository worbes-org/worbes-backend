package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ItemClass extends BaseEntity {

    @Id
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    public static ItemClass create(ItemClassesIndexResponse.ItemClassDto itemClassDto) {
        return ItemClass.builder()
                .id(itemClassDto.getId())
                .name(itemClassDto.getName())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemClass itemClass = (ItemClass) o;
        return Objects.equals(id, itemClass.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
