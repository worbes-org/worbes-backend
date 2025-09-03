package com.worbes.application.auction.model;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class AuctionTrendStatistics {

    private final List<AuctionTrendPoint> trendPoints;

    /**
     * 전체 기간 동안의 평균 최저가를 100 단위로 반올림하여 반환합니다.
     *
     * @return 평균 최저가 (100 단위), 데이터 없으면 0
     */
    public long getAverageLowestPrice() {
        double average = trendPoints.stream()
                .filter(tp -> tp.lowestPrice() != null)
                .mapToLong(AuctionTrendPoint::lowestPrice)
                .average()
                .orElse(0);
        return Math.round(average / 100.0) * 100;
    }

    /**
     * 전체 기간 동안의 중앙 최저가를 100 단위로 반올림하여 반환합니다.
     *
     * @return 중앙 최저가 (100 단위), 데이터 없으면 0
     */
    public long getMedianLowestPrice() {
        List<Long> sortedPrices = trendPoints.stream()
                .map(AuctionTrendPoint::lowestPrice)
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        int size = sortedPrices.size();
        if (size == 0) return 0;

        long median;
        if (size % 2 == 1) {
            median = sortedPrices.get(size / 2);
        } else {
            long lower = sortedPrices.get(size / 2 - 1);
            long upper = sortedPrices.get(size / 2);
            median = (lower + upper) / 2;
        }

        return Math.round(median / 100.0) * 100;
    }
}
