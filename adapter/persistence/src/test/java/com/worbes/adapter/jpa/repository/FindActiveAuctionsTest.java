package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("Integration::SearchAuctionRepository::findActiveAuctions")
public class FindActiveAuctionsTest {

    @Autowired
    private AuctionJpaRepository jpaRepository;

    @Autowired
    private SearchAuctionRepository auctionRepository;

    @DisplayName("조건과 active = true 필터로 조회하고 buyout 값 오름차순으로 정렬한다")
    @Test
    void findActiveAuctionsBy_shouldFilterAndSortByCoalescedBuyoutAsc() {
        // given
        jpaRepository.save(AuctionEntity.builder()
                .auctionId(1L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(null)
                .buyout(300L)
                .active(true)
                .region(RegionType.KR)
                .realmId(null)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(2L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(null)
                .buyout(150L)
                .active(true)
                .region(RegionType.KR)
                .realmId(null)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(3L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(null)
                .buyout(60L)
                .active(true)
                .region(RegionType.KR)
                .realmId(null)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(4L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(null)
                .buyout(600L)
                .active(false) // 비활성
                .region(RegionType.KR)
                .realmId(null)
                .build());

        // when
        List<Auction> results = auctionRepository.findActiveAuctions(
                100L, RegionType.KR, null
        );

        // then
        assertThat(results)
                .hasSize(3)
                .extracting(Auction::getId)
                .containsExactly(3L, 2L, 1L);
    }

    @DisplayName("조건과 active = true 필터로 조회하고 unit price 값 오름차순으로 정렬한다")
    @Test
    void findActiveAuctionsBy_shouldFilterAndSortByCoalescedUnitPriceAsc() {
        // given
        jpaRepository.save(AuctionEntity.builder()
                .auctionId(1L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(1000L)
                .buyout(null)
                .active(true)
                .region(RegionType.KR)
                .realmId(1L)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(2L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(500L)
                .buyout(null)
                .active(true)
                .region(RegionType.KR)
                .realmId(1L)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(3L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(300L)
                .buyout(null)
                .active(true)
                .region(RegionType.KR)
                .realmId(1L)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(4L)
                .itemId(100L)
                .quantity(5L)
                .unitPrice(100L)
                .buyout(null)
                .active(false) // 비활성
                .region(RegionType.KR)
                .realmId(1L)
                .build());

        // when
        List<Auction> results = auctionRepository.findActiveAuctions(
                100L, RegionType.KR, 1L
        );

        // then
        assertThat(results)
                .hasSize(3)
                .extracting(Auction::getId)
                .containsExactly(3L, 2L, 1L);
    }
}
