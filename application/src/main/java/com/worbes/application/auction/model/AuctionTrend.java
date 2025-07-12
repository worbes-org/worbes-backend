package com.worbes.application.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Getter
public class AuctionTrend {
    private final List<AuctionTrendPoint> trendPoints;

    public Double getLowestPriceChangeRateSince(LocalDateTime baseTime) {
        if (trendPoints == null || trendPoints.isEmpty()) return null;

        // trends를 시간순 정렬
        List<AuctionTrendPoint> sorted = trendPoints.stream()
                .sorted(Comparator.comparing(AuctionTrendPoint::time))
                .toList();

        // baseTime과 가장 가까운 데이터 찾기 (baseTime보다 같거나 이전 중 가장 최근)
        AuctionTrendPoint baseTrend = null;
        for (AuctionTrendPoint t : sorted) {
            if (!t.time().isAfter(Instant.from(baseTime))) {
                baseTrend = t;
            } else {
                break;
            }
        }

        // now와 가장 가까운 데이터 (가장 최근 데이터)
        AuctionTrendPoint nowTrend = sorted.get(sorted.size() - 1);

        if (baseTrend == null || nowTrend.lowestPrice() == null || baseTrend.lowestPrice() == null) return null;
        if (baseTrend.lowestPrice() == 0) return null; // 0으로 나누기 방지

        return ((double) (nowTrend.lowestPrice() - baseTrend.lowestPrice()) / baseTrend.lowestPrice()) * 100;
    }
}
