package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.model.AuctionTrend;
import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetActiveAuctionUseCase;
import com.worbes.application.auction.port.in.GetAuctionSummaryUseCase;
import com.worbes.application.auction.port.in.GetAuctionTrendUseCase;
import com.worbes.application.auction.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionQueryService implements GetAuctionSummaryUseCase, GetActiveAuctionUseCase, GetAuctionTrendUseCase {

    private final AuctionQueryRepository auctionQueryRepository;
    private final AuctionSummaryQueryRepository auctionSummaryQueryRepository;
    private final AuctionTrendQueryRepository auctionTrendQueryRepository;

    public List<AuctionSummary> getSummary(AuctionSummarySearchCondition condition) {
        if (condition.items().isEmpty()) return Collections.emptyList();

        return auctionSummaryQueryRepository.findSummary(condition);
    }

    @Override
    public Map<Long, Integer> groupActiveAuctionsByPrice(AuctionSearchCondition condition) {
        return auctionQueryRepository.findActive(condition).stream()
                .collect(Collectors.groupingBy(
                        Auction::getPrice,
                        TreeMap::new,
                        Collectors.summingInt(Auction::getQuantity)
                ));
    }

    @Override
    public AuctionTrend getHourlyTrend(AuctionTrendSearchCondition condition) {
        List<AuctionTrendPoint> trendPoints = auctionTrendQueryRepository.findHourlyTrend(condition);

        return new AuctionTrend(trendPoints);
    }
}
