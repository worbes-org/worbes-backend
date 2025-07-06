package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SyncAuctionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_COUNT;
import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_SNAPSHOT;

@Slf4j
@RequiredArgsConstructor
public class CreateAuctionWriter implements ItemWriter<Auction>, StepExecutionListener {

    private final SyncAuctionUseCase syncAuctionUseCase;
    private int totalUpdatedOrInsertedAuction = 0;

    @Override
    public void write(Chunk<? extends Auction> chunk) {
        if (chunk.isEmpty()) return;
        List<Auction> chunkedAuction = new ArrayList<>(chunk.getItems());
        int updatedOrInsertedAuctions = syncAuctionUseCase.syncAll(chunkedAuction);
        totalUpdatedOrInsertedAuction += updatedOrInsertedAuctions;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();

        stepExecution.setWriteCount(totalUpdatedOrInsertedAuction);

        jobContext.remove(AUCTION_SNAPSHOT.getKey());
        jobContext.remove(AUCTION_COUNT.getKey());

        return ExitStatus.COMPLETED;
    }
}
