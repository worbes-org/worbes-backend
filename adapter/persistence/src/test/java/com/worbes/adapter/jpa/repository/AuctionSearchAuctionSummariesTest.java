package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("Integration::SearchAuctionRepository::")
public class AuctionSearchAuctionSummariesTest {

    @Autowired
    private SearchAuctionRepository auctionRepository;

    @Autowired
    private AuctionJpaRepository jpaRepository;

    @Test
    @DisplayName("searchAuctionSummaries는 아이템별 최저가 및 총 수량을 반환한다")
    void searchAuctionSummaries_shouldReturnLowestPricesAndTotalQuantityPerItem() {
        // given
        RegionType region = RegionType.KR;
        Long realmId = 101L;
        Long itemId1 = 1001L;
        Long itemId2 = 1002L;

        // 아이템 1
        jpaRepository.saveAll(List.of(
                AuctionEntity.builder()
                        .auctionId(1L)
                        .itemId(itemId1)
                        .unitPrice(500L)
                        .buyout(null)
                        .quantity(3L)
                        .active(true)
                        .region(region)
                        .realmId(realmId)
                        .build(),
                AuctionEntity.builder()
                        .auctionId(2L)
                        .itemId(itemId1)
                        .unitPrice(300L)
                        .buyout(null)
                        .quantity(2L)
                        .active(true)
                        .region(region)
                        .realmId(realmId)
                        .build()
        ));

        // 아이템 2
        jpaRepository.saveAll(List.of(
                AuctionEntity.builder()
                        .auctionId(3L)
                        .itemId(itemId2)
                        .unitPrice(800L)
                        .buyout(null)
                        .quantity(4L)
                        .active(true)
                        .region(region)
                        .realmId(realmId)
                        .build()
        ));

        // when
        List<SearchAuctionSummaryResult> results = auctionRepository.searchSummaries(
                new SearchAuctionCommand(
                        region,
                        realmId,
                        0,
                        20
                ),
                Set.of(itemId1, itemId2)
        );

        // then
        assertThat(results).hasSize(2);

        SearchAuctionSummaryResult summary1 = results.stream()
                .filter(s -> s.itemId().equals(itemId1))
                .findFirst()
                .orElseThrow();

        assertThat(summary1.minPrice()).isEqualTo(300L);
        assertThat(summary1.available()).isEqualTo(5L); // 3 + 2

        SearchAuctionSummaryResult summary2 = results.stream()
                .filter(s -> s.itemId().equals(itemId2))
                .findFirst()
                .orElseThrow();

        assertThat(summary2.minPrice()).isEqualTo(800L);
        assertThat(summary2.available()).isEqualTo(4L);
    }
}
