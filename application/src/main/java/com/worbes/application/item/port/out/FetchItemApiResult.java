package com.worbes.application.item.port.out;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record FetchItemApiResult(
        @NotNull Long id,
        @NotNull Map<String, String> name,
        @NotNull String quality,
        @NotNull String inventoryType,
        @NotNull Long classId,
        @NotNull Long subclassId,
        @NotNull Integer level,
        @NotNull Boolean isStackable
) {
}
