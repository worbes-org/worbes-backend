package com.worbes.batch.config;

import com.worbes.adapter.batch.item.CreateItemWriter;
import com.worbes.adapter.jpa.auction.AuctionJpaRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Configuration
public class BatchCreateItemJobConfig {

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
            @Qualifier("auctionItemIdReader") RepositoryItemReader<Long> auctionItemIdReader,
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
    public RepositoryItemReader<Long> auctionItemIdReader(AuctionJpaRepository repository) {
        return new RepositoryItemReaderBuilder<Long>()
                .name("auctionItemIdReader")
                .repository(repository)
                .methodName("findDistinctActiveItemIds")
                .pageSize(1000)
                .sorts(Map.of("itemId", Sort.Direction.ASC))
                .build();
    }
}
