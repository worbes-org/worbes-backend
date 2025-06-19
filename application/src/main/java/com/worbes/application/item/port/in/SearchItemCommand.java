package com.worbes.application.item.port.in;

public record SearchItemCommand(
        Long itemClassId,
        Long itemSubclassId,
        String name
) {
}
