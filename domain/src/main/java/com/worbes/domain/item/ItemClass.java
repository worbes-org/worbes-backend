package com.worbes.domain.item;

import com.worbes.domain.shared.LocalizedName;
import lombok.Getter;

@Getter
public class ItemClass {

    private final Long id;
    private final LocalizedName name;

    public ItemClass(Long id, LocalizedName name) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");
        if (name == null) throw new IllegalArgumentException("name은 null일 수 없습니다.");
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemClass itemClass = (ItemClass) o;
        return id.equals(itemClass.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
