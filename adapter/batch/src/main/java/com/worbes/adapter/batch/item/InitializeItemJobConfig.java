package com.worbes.adapter.batch.item;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class InitializeItemJobConfig {

    @Bean
    public Job initializeItemJob(
            JobRepository jobRepository,
            @Qualifier("initializeItemStep") Step initializeItemStep
    ) {
        return new JobBuilder("initializeItem", jobRepository)
                .start(initializeItemStep)
                .build();
    }

    @Bean
    @JobScope
    public Step initializeItemStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("auctionItemIdReader") JdbcPagingItemReader<Long> auctionItemIdReader,
            CreateItemWriter createItemWriter
    ) {
        return new StepBuilder("initializeItemStep", jobRepository)
                .<Long, Long>chunk(50, transactionManager)
                .reader(auctionItemIdReader)
                .writer(createItemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Long> auctionItemIdReader(
            @Qualifier("appDataSource") DataSource dataSource,
            @Qualifier("auctionItemIdQueryProvider") PagingQueryProvider queryProvider
    ) {
        return new JdbcPagingItemReaderBuilder<Long>()
                .name("auctionItemIdReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .rowMapper((rs, rowNum) -> rs.getLong("item_id"))
                .pageSize(1000)
                .build();
    }

    @Bean
    @StepScope
    public SqlPagingQueryProviderFactoryBean auctionItemIdQueryProvider(
            @Qualifier("appDataSource") DataSource dataSource
    ) {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT DISTINCT item_id");
        provider.setFromClause("FROM auction");
        provider.setWhereClause("WHERE active = true");
        provider.setSortKey("item_id");

        return provider;
    }
}
