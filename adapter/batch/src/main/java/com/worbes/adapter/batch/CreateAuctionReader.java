package com.worbes.adapter.batch;

import com.worbes.application.auction.model.Auction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

import java.util.Iterator;
import java.util.List;

import static com.worbes.adapter.batch.SyncAuctionParameters.AUCTION_SNAPSHOT;

@Slf4j
public class CreateAuctionReader implements ItemReader<Auction>, StepExecutionListener {

    private Iterator<Auction> auctionIterator;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext context = stepExecution.getJobExecution().getExecutionContext();
        Object obj = context.get(AUCTION_SNAPSHOT.getKey());

        if (!(obj instanceof List<?> snapshot)) {
            throw new IllegalStateException("Invalid snapshot data in ExecutionContext");
        }

        auctionIterator = snapshot.stream()
                .filter(Auction.class::isInstance)
                .map(Auction.class::cast)
                .iterator();
    }

    @Override
    public Auction read() {
        return auctionIterator.hasNext() ? auctionIterator.next() : null;
    }
}
