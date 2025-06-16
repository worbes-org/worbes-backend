package com.worbes.adapter.batch;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionCommand;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
import java.util.Optional;

import static com.worbes.adapter.batch.SyncAuctionParameters.*;

@Slf4j
@RequiredArgsConstructor
public class FetchAuctionTasklet implements Tasklet {

    private final FetchAuctionUseCase fetchAuctionUseCase;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        JobParameters jobParameters = jobExecution.getJobParameters();

        String region = Optional.ofNullable(jobParameters.getString(REGION.getKey()))
                .orElseThrow(() -> new IllegalArgumentException("Region not found"));
        Long realmId = jobParameters.getLong(REALM_ID.getKey(), null);

        List<Auction> snapshot = fetchAuctionUseCase.fetchAuctions(
                new FetchAuctionCommand(RegionType.valueOf(region), realmId)
        );
        log.info("경매 스냅샷 조회 완료 - region={}, realmId={}, 수집 수={}", region, realmId, snapshot.size());

        ExecutionContext jobContext = jobExecution.getExecutionContext();
        jobContext.put(AUCTION_SNAPSHOT.getKey(), snapshot);
        jobContext.put(AUCTION_COUNT.getKey(), snapshot.size());

        return RepeatStatus.FINISHED;
    }
}
