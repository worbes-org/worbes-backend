package com.worbes.batch.config;

import com.worbes.adapter.batch.auction.*;
import com.worbes.adapter.jpa.auction.AuctionJpaRepository;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class BatchSyncAuctionJobConfig {

    @Bean
    public Job auctionSyncJob(
            JobRepository jobRepository,
            @Qualifier("fetchAuctionStep") Step fetchStep,
            @Qualifier("closeAuctionStep") Step closeStep,
            @Qualifier("createAuctionStep") Step createStep,
            @Qualifier("createAuctionSnapshotStep") Step createSnapshotStep
    ) {
        return new JobBuilder("auctionSync", jobRepository)
                .start(fetchStep)
                .next(closeStep)
                .next(createStep)
                .next(createSnapshotStep)
                .build();
    }

    @Bean
    @JobScope
    public Step fetchAuctionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FetchAuctionTasklet tasklet
    ) {
        return new StepBuilder("fetchAuctionStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step closeAuctionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("closeAuctionReader") RepositoryItemReader<Long> reader,
            CloseAuctionProcessor processor,
            CloseAuctionWriter writer
    ) {
        return new StepBuilder("closeAuctionStep", jobRepository)
                .<Long, Long>chunk(1000, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Long> closeAuctionReader(
            AuctionJpaRepository repository,
            @Value("#{jobParameters['region']}") String region,
            @Value("#{jobParameters['realmId']}") Long realmId
    ) {
        List<Object> args = new ArrayList<>();
        args.add(RegionType.fromValue(region));
        args.add(realmId);

        return new RepositoryItemReaderBuilder<Long>()
                .name("closeAuctionReader")
                .repository(repository)
                .methodName("findActiveAuctionsByRegionAndRealmId")
                .arguments(args)
                .pageSize(1000)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    @JobScope
    public Step createAuctionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SyncAuctionReader syncAuctionReader,
            SyncAuctionWriter writer
    ) {
        return new StepBuilder("createAuctionStep", jobRepository)
                .<Auction, Auction>chunk(1000, transactionManager)
                .reader(syncAuctionReader)
                .writer(writer)
                .build();
    }

    @Bean
    @JobScope
    public Step createAuctionSnapshotStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CreateAuctionSnapshotTasklet tasklet
    ) {
        return new StepBuilder("createAuctionSnapshotStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
