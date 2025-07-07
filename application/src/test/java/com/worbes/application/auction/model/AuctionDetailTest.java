package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import com.worbes.application.auction.port.out.AuctionTrend;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionDetailTest {

    @DisplayName("가격별 수량합 오름차순 정렬")
    @Test
    void testGetAvailable() {
        // given
        Item item = Item.builder().id(1L).name(Map.of("ko_KR", "테스트아이템")).build();
        Auction a1 = Auction.builder().id(1L).itemId(1L).quantity(10L).price(1000L).build();
        Auction a2 = Auction.builder().id(2L).itemId(1L).quantity(5L).price(900L).build();
        Auction a3 = Auction.builder().id(3L).itemId(1L).quantity(3L).price(1000L).build();
        Auction a4 = Auction.builder().id(4L).itemId(1L).quantity(7L).price(1200L).build();
        Auction a5 = Auction.builder().id(5L).itemId(1L).quantity(2L).price(900L).build();
        List<Auction> auctions = List.of(a1, a2, a3, a4, a5);
        AuctionDetail detail = new AuctionDetail(item, auctions, List.of());

        // when
        Map<Long, Long> available = detail.getAvailable();

        // then: 가격 오름차순, 수량 합산
        assertThat(available.keySet()).containsExactly(900L, 1000L, 1200L);
        assertThat(available.get(900L)).isEqualTo(7L); // 5+2
        assertThat(available.get(1000L)).isEqualTo(13L); // 10+3
        assertThat(available.get(1200L)).isEqualTo(7L);
    }

    @DisplayName("현재부터 과거 24시간, 3/6/12/24시간 전 변동률 계산")
    @Test
    void testLowestPriceChangeRateForVariousHoursAgo() {
        // given
        Item item = Item.builder().id(1L).name(Map.of("ko_KR", "테스트아이템")).build();
        List<Auction> auctions = List.of();
        LocalDateTime now = LocalDateTime.of(2024, 5, 2, 0, 0);
        // 0, 3, 6, 9, 12, 15, 18, 21, 24시간 전 데이터 생성
        List<AuctionTrend> trends = List.of(
                new AuctionTrend(now.minusHours(24), 100, 1000L),
                new AuctionTrend(now.minusHours(21), 100, 1100L),
                new AuctionTrend(now.minusHours(18), 100, 1200L),
                new AuctionTrend(now.minusHours(15), 100, 1300L),
                new AuctionTrend(now.minusHours(12), 100, 1400L),
                new AuctionTrend(now.minusHours(9), 100, 1500L),
                new AuctionTrend(now.minusHours(6), 100, 1600L),
                new AuctionTrend(now.minusHours(3), 100, 1700L),
                new AuctionTrend(now, 100, 1800L)
        );
        AuctionDetail detail = new AuctionDetail(item, auctions, trends);

        // when
        Double rate3 = detail.getLowestPriceChangeRateSince(now.minusHours(3));
        Double rate6 = detail.getLowestPriceChangeRateSince(now.minusHours(6));
        Double rate12 = detail.getLowestPriceChangeRateSince(now.minusHours(12));
        Double rate24 = detail.getLowestPriceChangeRateSince(now.minusHours(24));

        // then
        assertThat(rate3).isEqualTo(((1800.0 - 1700.0) / 1700.0) * 100);
        assertThat(rate6).isEqualTo(((1800.0 - 1600.0) / 1600.0) * 100);
        assertThat(rate12).isEqualTo(((1800.0 - 1400.0) / 1400.0) * 100);
        assertThat(rate24).isEqualTo(((1800.0 - 1000.0) / 1000.0) * 100);
    }
}
