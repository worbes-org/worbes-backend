package com.worbes.application.item.model;

import lombok.Getter;

@Getter
public enum QualityType {
    POOR(1),
    COMMON(2),
    UNCOMMON(3),
    RARE(4),
    EPIC(5),
    LEGENDARY(6);

    private final int value;

    QualityType(int value) {
        this.value = value;
    }

    public static QualityType fromValue(int value) {
        for (QualityType tier : values()) {
            if (tier.value == value) {
                return tier;
            }
        }
        throw new IllegalArgumentException("Invalid quality: " + value);
    }
}
