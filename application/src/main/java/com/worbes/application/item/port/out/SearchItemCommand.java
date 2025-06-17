package com.worbes.application.item.port.out;

import com.worbes.application.common.model.LocaleCode;

public record SearchItemCommand(
        Long itemClassId,
        Long itemSubclassId,
        String name,
        LocaleCode locale
) {

}
