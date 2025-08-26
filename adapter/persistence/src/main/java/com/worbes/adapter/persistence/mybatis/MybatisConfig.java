package com.worbes.adapter.persistence.mybatis;

import com.worbes.adapter.persistence.mybatis.handler.JsonbListTypeHandler;
import com.worbes.adapter.persistence.mybatis.handler.JsonbToMapTypeHandler;
import com.worbes.adapter.persistence.mybatis.handler.RegionTypeHandler;
import com.worbes.application.realm.model.RegionType;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@MapperScan("com.worbes.adapter.persistence.mybatis")
public class MybatisConfig {
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            configuration.getTypeHandlerRegistry()
                    .register(RegionType.class, RegionTypeHandler.class);
            configuration.getTypeHandlerRegistry()
                    .register(Map.class, JsonbToMapTypeHandler.class);
            configuration.getTypeHandlerRegistry()
                    .register(List.class, JsonbListTypeHandler.class);
        };
    }
}
