package com.worbes.adapter.jpa.item;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSubclassId implements Serializable {

    @Column(name = "item_subclass_id")
    private Long subclassId;

    @Column(name = "item_class_id")
    private Long itemClassId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemSubclassId that = (ItemSubclassId) o;
        return Objects.equals(subclassId, that.subclassId) && Objects.equals(itemClassId, that.itemClassId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subclassId, itemClassId);
    }
}
