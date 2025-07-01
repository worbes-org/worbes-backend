package com.worbes.web;

import com.worbes.application.realm.model.RegionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RegionTypeConverter implements Converter<String, RegionType> {

    @Override
    public RegionType convert(String source) {
        return RegionType.fromValue(source);
    }
}
