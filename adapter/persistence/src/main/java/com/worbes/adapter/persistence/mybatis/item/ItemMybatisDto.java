package com.worbes.adapter.persistence.mybatis.item;

import java.util.Map;

public record ItemMybatisDto(
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
        Long displayId
) {
}
