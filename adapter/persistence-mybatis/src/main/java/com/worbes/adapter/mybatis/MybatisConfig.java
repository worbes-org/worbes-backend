package com.worbes.adapter.mybatis;

import com.worbes.adapter.mybatis.common.JsonbToMapTypeHandler;
import com.worbes.adapter.mybatis.common.RegionTypeHandler;
import com.worbes.application.realm.model.RegionType;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@MapperScan("com.worbes.adapter.mybatis")
public class MybatisConfig {
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry()
                    .register(RegionType.class, RegionTypeHandler.class);
            configuration.getTypeHandlerRegistry()
                    .register(Map.class, JsonbToMapTypeHandler.class);
        };
    }
}
