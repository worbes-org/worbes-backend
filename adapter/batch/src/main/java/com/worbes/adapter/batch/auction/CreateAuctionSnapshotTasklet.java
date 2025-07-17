package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.CreateAuctionSnapshotUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.*;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class CreateAuctionSnapshotTasklet implements Tasklet {

    private final CreateAuctionSnapshotUseCase createAuctionSnapshotUseCase;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        JobParameters jobParameters = jobExecution.getJobParameters();

        RegionType region = Optional.ofNullable(jobParameters.getString(REGION.getKey()))
                .map(RegionType::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("Region not found"));
        Instant time = Optional.ofNullable(jobParameters.getLocalDateTime(AUCTION_DATE.getKey()))
                .map(localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant())
                .orElseThrow(() -> new IllegalArgumentException("Time not found"));
        Long realmId = jobParameters.getLong(REALM_ID.getKey(), null);

        int result = createAuctionSnapshotUseCase.execute(region, realmId, time);
        contribution.incrementWriteCount(result);

        return RepeatStatus.FINISHED;
    }
}
