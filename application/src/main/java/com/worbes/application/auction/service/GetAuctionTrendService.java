package com.worbes.application.auction.service;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.model.AuctionTrendStatistics;
import com.worbes.application.auction.port.in.GetAuctionTrendQuery;
import com.worbes.application.auction.port.in.GetAuctionTrendResult;
import com.worbes.application.auction.port.in.GetAuctionTrendUseCase;
import com.worbes.application.auction.port.out.FindAuctionTrendPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAuctionTrendService implements GetAuctionTrendUseCase {

    private final FindAuctionTrendPort findAuctionTrendPort;

    @Override
    public GetAuctionTrendResult execute(GetAuctionTrendQuery query) {
        List<AuctionTrendPoint> trendPoints = findAuctionTrendPort.findTrendsBy(query);
        AuctionTrendStatistics statistics = new AuctionTrendStatistics(trendPoints);
        long averageLowestPrice = statistics.getAverageLowestPrice();
        long medianLowestPrice = statistics.getMedianLowestPrice();

        return new GetAuctionTrendResult(averageLowestPrice, medianLowestPrice, trendPoints);
    }
}
