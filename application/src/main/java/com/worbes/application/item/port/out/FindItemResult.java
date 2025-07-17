package com.worbes.application.item.port.out;

import java.time.LocalDateTime;
import java.util.Map;

public record FindItemResult(
        Long id,
        Map<String, String> name,
        Integer level,
        Long classId,
        Long subclassId,
        String inventoryType,
        Integer quality,
        String icon,
        Integer craftingTier,
        Boolean isStackable,
        Integer expansionId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
