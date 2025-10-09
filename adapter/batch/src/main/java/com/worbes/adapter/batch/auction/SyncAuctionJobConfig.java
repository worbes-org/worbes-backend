package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SyncAuctionJobConfig {

    @Bean
    public Job auctionSyncJob(
            JobRepository jobRepository,
            @Qualifier("deleteAuctionSnapshotStep") Step fetchStep,
            @Qualifier("deleteAuctionStep") Step deleteStep,
            @Qualifier("createAuctionStep") Step createStep,
            @Qualifier("createAuctionSnapshotStep") Step createSnapshotStep
    ) {
        return new JobBuilder("auctionSync", jobRepository)
                .start(fetchStep)
                .next(deleteStep)
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
    public Step deleteAuctionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            DeleteAuctionTasklet tasklet
    ) {
        return new StepBuilder("deleteAuctionStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step createAuctionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            CreateAuctionReader createAuctionReader,
            CreateAuctionWriter writer
    ) {
        return new StepBuilder("createAuctionStep", jobRepository)
                .<Auction, Auction>chunk(1000, transactionManager)
                .reader(createAuctionReader)
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
