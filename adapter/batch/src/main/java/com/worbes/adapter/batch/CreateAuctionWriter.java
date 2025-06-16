package com.worbes.adapter.batch;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.CreateAuctionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

import static com.worbes.adapter.batch.SyncAuctionParameters.AUCTION_COUNT;
import static com.worbes.adapter.batch.SyncAuctionParameters.AUCTION_SNAPSHOT;

@Slf4j
@RequiredArgsConstructor
public class CreateAuctionWriter implements ItemWriter<Auction>, StepExecutionListener, ItemStream {

    private static final String TOTAL_SAVED_KEY = "createAuctionWriter.totalSavedCount";
    private final CreateAuctionUseCase createAuctionUseCase;
    private int totalSavedCount = 0;

    @Override
    public void write(Chunk<? extends Auction> chunk) {
        List<Auction> chunkedAuction = new ArrayList<>(chunk.getItems());
        if (chunkedAuction.isEmpty()) return;
        int savedCount = createAuctionUseCase.createAuctions(chunkedAuction);
        totalSavedCount += savedCount;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (executionContext.containsKey(TOTAL_SAVED_KEY)) {
            totalSavedCount = executionContext.getInt(TOTAL_SAVED_KEY);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        executionContext.putInt(TOTAL_SAVED_KEY, totalSavedCount);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();

        int auctionCount = jobContext.getInt(AUCTION_COUNT.getKey());
        if (auctionCount <= 0 || auctionCount < totalSavedCount) {
            return ExitStatus.FAILED;
        }
        stepExecution.setWriteCount(totalSavedCount);
        stepExecution.setFilterCount(auctionCount - totalSavedCount);

        jobContext.remove(AUCTION_SNAPSHOT.getKey());
        jobContext.remove(AUCTION_COUNT.getKey());
        jobContext.remove(TOTAL_SAVED_KEY);

        return ExitStatus.COMPLETED;
    }
}
