package com.worbes.adapter.persistence;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.app")
    public DataSourceProperties appDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource appDataSource() {
        return appDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }
}
