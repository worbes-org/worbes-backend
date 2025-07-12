package com.worbes.application.item.port.out;

public record ItemSearchCondition(
        Long classId,
        Long subclassId,
        String name
) {
}
