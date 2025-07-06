package com.worbes.application.item.port.in;

public record SearchItemCondition(
        Long itemClassId,
        Long itemSubclassId,
        String name
) {
}
