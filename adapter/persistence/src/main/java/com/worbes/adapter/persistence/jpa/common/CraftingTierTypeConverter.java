package com.worbes.adapter.persistence.jpa.common;

import com.worbes.application.item.model.CraftingTierType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class CraftingTierTypeConverter implements AttributeConverter<CraftingTierType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CraftingTierType attribute) {
        return Optional.ofNullable(attribute).map(CraftingTierType::getValue).orElse(null);
    }

    @Override
    public CraftingTierType convertToEntityAttribute(Integer dbData) {
        return Optional.ofNullable(dbData).map(CraftingTierType::fromValue).orElse(null);
    }
}
