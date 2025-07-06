package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.EndAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.REALM_ID;
import static com.worbes.adapter.batch.auction.SyncAuctionParameters.REGION;

@Slf4j
@RequiredArgsConstructor
public class CloseAuctionWriter implements ItemWriter<Long>, StepExecutionListener {

    private final EndAuctionUseCase endAuctionUseCase;

    private RegionType region;
    private Long realmId;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters jobParameters = stepExecution.getJobExecution().getJobParameters();
        this.region = Optional.ofNullable(jobParameters.getString(REGION.getKey()))
                .map(RegionType::valueOf)
                .orElseThrow(() -> new IllegalStateException("Region not found"));
        this.realmId = jobParameters.getLong(REALM_ID.getKey(), null);
    }

    @Override
    public void write(Chunk<? extends Long> chunk) {
        Set<Long> toCloseAuctionIds = new HashSet<>(chunk.getItems());
        endAuctionUseCase.end(region, realmId, toCloseAuctionIds);
    }
}
