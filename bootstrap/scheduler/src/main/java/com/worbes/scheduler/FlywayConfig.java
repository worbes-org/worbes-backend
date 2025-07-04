package com.worbes.scheduler;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway batchFlyway(@Qualifier("batchDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/batch")
                .defaultSchema("public")
                .load();
    }

    @Bean
    public Flyway appFlyway(@Qualifier("appDataSource") DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/app")
                .defaultSchema("public")
                .load();
    }
}
