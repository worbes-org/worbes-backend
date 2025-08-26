package com.worbes.application.item.model;

import lombok.Getter;

@Getter
public enum CraftingTierType {
    FIRST(1),
    SECOND(2),
    THIRD(3);

    private final int value;

    CraftingTierType(int value) {
        this.value = value;
    }

    public static CraftingTierType fromValue(int value) {
        for (CraftingTierType tier : values()) {
            if (tier.value == value) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Invalid tier: " + value);
    }
}
