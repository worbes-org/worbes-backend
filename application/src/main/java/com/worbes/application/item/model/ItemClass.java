package com.worbes.application.item.model;

import com.worbes.application.common.model.LocalizedName;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ItemClass {

    private final Long id;
    private final LocalizedName name;
    private final List<ItemSubclass> subclasses = new ArrayList<>();

    public ItemClass(Long id, LocalizedName name) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");
        if (name == null) throw new IllegalArgumentException("name은 null일 수 없습니다.");
        this.id = id;
        this.name = name;
    }

    public ItemClass(Long id, Map<String, String> name) {
        if (id == null || name == null) throw new IllegalArgumentException("필수값 누락");
        this.id = id;
        this.name = LocalizedName.fromRaw(name);
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

    public void addSubclasses(List<ItemSubclass> newSubclasses) {
        // 이미 존재하는 subclassId는 무시
        Set<Long> existingIds = subclasses.stream()
                .map(ItemSubclass::getSubclassId)
                .collect(Collectors.toSet());

        List<ItemSubclass> filtered = newSubclasses.stream()
                .filter(sub -> !existingIds.contains(sub.getSubclassId()))
                .toList();

        subclasses.addAll(filtered);
    }

    public Optional<ItemSubclass> findSubclass(Long subclassId) {
        return subclasses.stream()
                .filter(sub -> sub.getSubclassId().equals(subclassId))
                .findFirst();
    }
}
