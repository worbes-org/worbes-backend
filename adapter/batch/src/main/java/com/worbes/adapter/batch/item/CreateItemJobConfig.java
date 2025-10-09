package com.worbes.adapter.batch.item;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Configuration
public class CreateItemJobConfig {

    @Bean
    public Job createItemJob(
            JobRepository jobRepository,
            @Qualifier("createItemStep") Step initializeItemStep
    ) {
        return new JobBuilder("createItem", jobRepository)
                .start(initializeItemStep)
                .build();
    }

    @Bean
    @JobScope
    public Step createItemStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("auctionItemIdReader") JdbcPagingItemReader<Long> auctionItemIdReader,
            CreateItemWriter createItemWriter
    ) {
        return new StepBuilder("createItemStep", jobRepository)
                .<Long, Long>chunk(50, transactionManager)
                .reader(auctionItemIdReader)
                .writer(createItemWriter)
                .faultTolerant()
                .skip(ExecutionException.class)
                .skip(TimeoutException.class)
                .skipLimit(3)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Long> auctionItemIdReader(DataSource dataSource) {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("DISTINCT a.item_id");
        queryProvider.setFromClause("FROM auction a LEFT JOIN item i ON a.item_id = i.id");
        queryProvider.setWhereClause("i.id IS NULL");
        queryProvider.setSortKeys(Map.of("item_id", Order.DESCENDING));

        return new JdbcPagingItemReaderBuilder<Long>()
                .name("auctionItemIdReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .rowMapper((rs, rowNum) -> rs.getLong("item_id"))
                .pageSize(1000)
                .fetchSize(1000)
                .build();
    }
}
