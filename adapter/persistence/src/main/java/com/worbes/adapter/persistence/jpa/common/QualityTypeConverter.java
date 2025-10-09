package com.worbes.adapter.persistence.jpa.common;

import com.worbes.application.item.model.QualityType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

@Converter(autoApply = true)
public class QualityTypeConverter implements AttributeConverter<QualityType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(QualityType attribute) {
        return Optional.of(attribute)
                .map(QualityType::getValue)
                .orElseThrow(() -> new IllegalArgumentException("QualityType cannot be null"));
    }

    @Override
    public QualityType convertToEntityAttribute(Integer dbData) {
        return Optional.of(dbData)
                .map(QualityType::fromValue)
                .orElseThrow(() -> new IllegalArgumentException("QualityType value cannot be null"));
    }
}
