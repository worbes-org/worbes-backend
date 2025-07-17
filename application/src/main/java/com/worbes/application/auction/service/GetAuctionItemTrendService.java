package com.worbes.application.auction.service;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.model.AuctionTrendStatistics;
import com.worbes.application.auction.port.in.GetAuctionItemTrendQuery;
import com.worbes.application.auction.port.in.GetAuctionItemTrendResult;
import com.worbes.application.auction.port.in.GetAuctionItemTrendUseCase;
import com.worbes.application.auction.port.out.FindAuctionSnapshotHistoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAuctionItemTrendService implements GetAuctionItemTrendUseCase {

    private final FindAuctionSnapshotHistoryPort findAuctionSnapshotHistoryPort;

    @Override
    public GetAuctionItemTrendResult execute(GetAuctionItemTrendQuery query) {
        List<AuctionTrendPoint> trendPoints = findAuctionSnapshotHistoryPort.findHistory(query)
                .stream()
                .map(AuctionTrendPoint::new)
                .sorted(Comparator.comparing(AuctionTrendPoint::time))
                .toList();
        AuctionTrendStatistics statistics = new AuctionTrendStatistics(trendPoints);
        long averageLowestPrice = statistics.getAverageLowestPrice();
        long medianLowestPrice = statistics.getMedianLowestPrice();

        return new GetAuctionItemTrendResult(averageLowestPrice, medianLowestPrice, trendPoints);
    }
}
