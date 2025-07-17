package com.worbes.application.item.port.in;

public record SearchItemQuery(
        Long classId,
        Long subclassId,
        String name
) {
}
