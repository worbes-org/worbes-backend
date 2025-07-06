package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @BeforeEach
    void setUp() {
        // given
        jpaRepository.save(AuctionEntity.builder()
                .auctionId(1L)
                .itemId(100L)
                .quantity(5L)
                .price(1000L)
                .region(RegionType.KR)
                .realmId(null)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(2L)
                .itemId(100L)
                .quantity(5L)
                .price(500L)
                .region(RegionType.KR)
                .realmId(null)
                .build());

        jpaRepository.save(AuctionEntity.builder()
                .auctionId(3L)
                .itemId(100L)
                .quantity(5L)
                .price(300L)
                .region(RegionType.KR)
                .realmId(null)
                .build());

        AuctionEntity endedAuction = AuctionEntity.builder()
                .auctionId(4L)
                .itemId(100L)
                .quantity(5L)
                .price(100L)
                .region(RegionType.KR)
                .realmId(null)
                .endedAt(LocalDateTime.now())
                .build();
        jpaRepository.save(endedAuction);
    }

    @DisplayName("price 오름차순으로 정렬한다")
    @Test
    void findActiveAuctionsBy_shouldFilterAndSortByCoalescedBuyoutAsc() {
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
}
