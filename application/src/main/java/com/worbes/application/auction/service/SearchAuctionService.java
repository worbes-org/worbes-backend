package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.SearchAuctionSummaryQuery;
import com.worbes.application.auction.port.in.SearchAuctionSummaryResult;
import com.worbes.application.auction.port.in.SearchAuctionUseCase;
import com.worbes.application.auction.port.out.FindAuctionSnapshotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAuctionService implements SearchAuctionUseCase {

    private final FindAuctionSnapshotPort findAuctionSnapshotPort;

    @Override
    public List<SearchAuctionSummaryResult> execute(SearchAuctionSummaryQuery query) {
        return findAuctionSnapshotPort.findBy(query)
                .stream()
                .map(SearchAuctionSummaryResult::new)
                .toList();
    }
}
