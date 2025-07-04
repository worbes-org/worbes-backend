package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
