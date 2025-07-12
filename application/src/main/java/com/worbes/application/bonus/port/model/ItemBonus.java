package com.worbes.application.bonus.port.model;

public record ItemBonus(
        Long id,
        String suffix,
        Integer level,
        Integer baseLevel
) {
}
