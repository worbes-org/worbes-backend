package com.worbes.adapter.jpa;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "com.worbes.adapter.jpa",
        entityManagerFactoryRef = "appEntityManagerFactory",
        transactionManagerRef = "appTransactionManager"
)
@EnableConfigurationProperties(JpaProperties.class)
public class JpaConfig {

    private final JpaProperties jpaProperties;

    public JpaConfig(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

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

    @Bean
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(
            @Qualifier("appDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        return builder.dataSource(dataSource)
                .packages("com.worbes.adapter.jpa")
                .persistenceUnit("app")
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager appTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
