package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.CreateAuctionUseCase;
import com.worbes.application.auction.port.in.EndAuctionUseCase;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SyncAuctionJobConfig {

    @Bean
    public Job auctionSyncJob(
            JobRepository jobRepository,
            @Qualifier("fetchAuctionStep") Step fetchStep,
            @Qualifier("closeAuctionStep") Step closeStep,
            @Qualifier("createAuctionStep") Step createStep
    ) {
        return new JobBuilder("auctionSync", jobRepository)
                .start(fetchStep)
                .next(closeStep)
                .next(createStep)
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
    @StepScope
    public FetchAuctionTasklet fetchAuctionTasklet(FetchAuctionUseCase fetchAuctionUseCase) {
        return new FetchAuctionTasklet(fetchAuctionUseCase);
    }

    @Bean
    @JobScope
    public Step closeAuctionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            @Qualifier("closeAuctionReader") JdbcPagingItemReader<Long> reader,
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
    public CloseAuctionWriter closeAuctionWriter(EndAuctionUseCase endAuctionUseCase) {
        return new CloseAuctionWriter(endAuctionUseCase);
    }

    @Bean
    @StepScope
    public CloseAuctionProcessor closeAuctionProcessor() {
        return new CloseAuctionProcessor();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Long> closeAuctionReader(
            @Qualifier("appDataSource") DataSource dataSource,
            @Qualifier("auctionIdQueryProvider") PagingQueryProvider queryProvider,
            @Value("#{jobParameters['region']}") String region,
            @Value("#{jobParameters['realmId']}") Long realmId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("region", region);
        params.put("realmId", realmId);

        return new JdbcPagingItemReaderBuilder<Long>()
                .name("closeAuctionReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(params)
                .rowMapper((rs, rowNum) -> rs.getLong("auction_id"))
                .pageSize(1000)
                .build();
    }

    @Bean
    @StepScope
    public SqlPagingQueryProviderFactoryBean auctionIdQueryProvider(
            @Value("#{jobParameters['realmId']}") Long realmId,
            @Qualifier("appDataSource") DataSource dataSource
    ) {
        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT auction_id");
        provider.setFromClause("FROM auction");
        String whereClause = "WHERE region = :region AND ended_at IS NULL";
        if (realmId == null) {
            whereClause += " AND realm_id IS NULL";
        } else {
            whereClause += " AND realm_id = :realmId";
        }

        provider.setWhereClause(whereClause);
        provider.setSortKey("auction_id");

        return provider;
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
    @StepScope
    public CreateAuctionReader createAuctionReader() {
        return new CreateAuctionReader();
    }

    @Bean
    @StepScope
    public CreateAuctionWriter createAuctionWriter(CreateAuctionUseCase createAuctionUseCase) {
        return new CreateAuctionWriter(createAuctionUseCase);
    }
}
