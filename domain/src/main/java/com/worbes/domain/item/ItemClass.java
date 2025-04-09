package com.worbes.domain.item;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
@Builder
public class ItemClass {

    private Long id;
    private Map<String, String> name;

    public static ItemClass create(Long id, Map<String, String> name) {
        return ItemClass.builder()
                .id(id)
                .name(name)
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
