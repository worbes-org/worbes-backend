package com.worbes.adapter.persistence.jpa;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
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
        basePackages = "com.worbes.adapter.persistence.jpa",
        entityManagerFactoryRef = "appEntityManagerFactory",
        transactionManagerRef = "appTransactionManager"
)
@EnableConfigurationProperties(JpaProperties.class)
@RequiredArgsConstructor
public class JpaConfig {

    private final JpaProperties jpaProperties;

    @Bean
    public LocalContainerEntityManagerFactoryBean appEntityManagerFactory(
            @Qualifier("appDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        return builder.dataSource(dataSource)
                .packages("com.worbes.adapter.persistence.jpa")
                .persistenceUnit("app")
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager appTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    //Querydsl;
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
