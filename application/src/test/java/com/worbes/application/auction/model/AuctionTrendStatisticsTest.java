package com.worbes.application.auction.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionTrendStatisticsTest {
    @Test
    @DisplayName("평균 최저가를 계산할 수 있다")
    void averageLowestPrice() {
        List<AuctionTrendPoint> points = List.of(
                new AuctionTrendPoint(1L, 1001L, List.of(1L), Instant.now(), 1100L, 5),
                new AuctionTrendPoint(2L, 1001L, List.of(1L), Instant.now(), 1300L, 3),
                new AuctionTrendPoint(3L, 1001L, List.of(1L), Instant.now(), 1500L, 2)
        );

        AuctionTrendStatistics stats = new AuctionTrendStatistics(points);

        assertThat(stats.getAverageLowestPrice()).isEqualTo(1300);
    }

    @Test
    @DisplayName("중앙값 최저가를 계산할 수 있다")
    void medianLowestPrice() {
        List<AuctionTrendPoint> points = List.of(
                new AuctionTrendPoint(1L, 1001L, List.of(1L), Instant.now(), 1000L, 5),
                new AuctionTrendPoint(2L, 1001L, List.of(1L), Instant.now(), 2000L, 3),
                new AuctionTrendPoint(3L, 1001L, List.of(1L), Instant.now(), 3000L, 2)
        );

        AuctionTrendStatistics stats = new AuctionTrendStatistics(points);

        assertThat(stats.getMedianLowestPrice()).isEqualTo(2000);
    }

    @Test
    @DisplayName("데이터가 없으면 평균과 중앙값은 0이다")
    void emptyPointsReturnZero() {
        AuctionTrendStatistics stats = new AuctionTrendStatistics(List.of());

        assertThat(stats.getAverageLowestPrice()).isEqualTo(0);
        assertThat(stats.getMedianLowestPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("짝수 개일 경우 중앙값은 가운데 두 값의 평균이다")
    void medianLowestPriceEvenCount() {
        List<AuctionTrendPoint> points = List.of(
                new AuctionTrendPoint(1L, 1001L, List.of(), Instant.now(), 1000L, 5),
                new AuctionTrendPoint(2L, 1001L, List.of(), Instant.now(), 2000L, 3),
                new AuctionTrendPoint(3L, 1001L, List.of(), Instant.now(), 3000L, 2),
                new AuctionTrendPoint(4L, 1001L, List.of(), Instant.now(), 4000L, 1)
        );

        AuctionTrendStatistics stats = new AuctionTrendStatistics(points);

        assertThat(stats.getMedianLowestPrice()).isEqualTo(2500);
    }
}
