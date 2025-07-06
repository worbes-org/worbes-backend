package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_SNAPSHOT;

public class CloseAuctionProcessor implements ItemProcessor<Long, Long>, StepExecutionListener {

    private Set<Long> newAuctionIds;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext context = stepExecution.getJobExecution().getExecutionContext();
        Object obj = context.get(AUCTION_SNAPSHOT.getKey());

        if (!(obj instanceof List<?> snapshot)) {
            throw new IllegalStateException("Invalid snapshot data in ExecutionContext");
        }

        newAuctionIds = snapshot.stream()
                .filter(Auction.class::isInstance)
                .map(Auction.class::cast)
                .map(Auction::getId)
                .collect(Collectors.toSet());
    }

    @Override
    public Long process(Long auctionId) {
        if (newAuctionIds.contains(auctionId)) {
            return null;
        }
        return auctionId;
    }
}
