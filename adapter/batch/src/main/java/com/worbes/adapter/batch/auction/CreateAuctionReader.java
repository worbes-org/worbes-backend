package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameter.AUCTION_SNAPSHOT;

@Slf4j
@Component
@StepScope
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
