package com.worbes.adapter.batch.auction;

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
public class DeleteAuctionSnapshotJobConfig {

    @Bean
    public Job deleteAuctionSnapshotJob(
            JobRepository jobRepository,
            @Qualifier("deleteAuctionSnapshotStep") Step deleteAuctionSnapshotStep
    ) {
        return new JobBuilder("deleteAuctionSnapshotJob", jobRepository)
                .start(deleteAuctionSnapshotStep)
                .build();
    }

    @Bean
    @JobScope
    public Step deleteAuctionSnapshotStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            DeleteAuctionSnapshotTasklet tasklet
    ) {
        return new StepBuilder("deleteAuctionSnapshotStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }
}
