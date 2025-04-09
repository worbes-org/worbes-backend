package com.worbes.domain.item;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

@Getter
@Builder
public class ItemSubclass {

    private final Long subclassId;
    private final ItemClass itemClass;
    private final Map<String, String> displayName;
    private final Map<String, String> verboseName;

    public static ItemSubclass create(Long subclassId,
                                      ItemClass itemClass,
                                      Map<String, String> displayName,
                                      Map<String, String> verboseName) {
        return ItemSubclass.builder()
                .subclassId(subclassId)
                .itemClass(itemClass)
                .displayName(displayName)
                .verboseName(verboseName)
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
