package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionItemStatsQuery;
import com.worbes.application.auction.port.out.FindActiveAuctionPort;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FindActiveAuctionPortFindAllActiveByTest {

    @Autowired
    private FindActiveAuctionPort auctionRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    private static AuctionEntity createAuction(Long auctionId, Long itemId, int quantity, long price, RegionType region, Long realmId, java.time.Instant endedAt) {
        AuctionEntity entity = AuctionEntity.builder()
                .id(auctionId)
                .itemId(itemId)
                .quantity(quantity)
                .price(price)
                .region(region)
                .realmId(realmId)
                .build();
        entity.setEndedAt(endedAt);
        return entity;
    }

    @Nested
    @DisplayName("happy case")
    class HappyCase {
        @Test
        @DisplayName("endedAt이 null인 경매만 반환된다")
        void shouldReturnOnlyActiveAuctions_whenEndedAtIsNull() {
            // given
            AuctionEntity active1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L, null);
            AuctionEntity ended = createAuction(2L, 100L, 1, 600L, RegionType.KR, 101L, Instant.now());
            auctionJpaRepository.saveAll(List.of(active1, ended));

            // when
            List<Auction> result = auctionRepository.findActive(
                    new GetAuctionItemStatsQuery(
                            RegionType.KR, 101L, 100L, null)
            );

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("여러 active 경매가 가격 오름차순으로 반환된다")
        void shouldReturnMultipleActiveAuctionsSortedByPrice() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 700L, RegionType.KR, 101L, null);
            AuctionEntity a2 = createAuction(2L, 100L, 2, 500L, RegionType.KR, 101L, null);
            auctionJpaRepository.saveAll(List.of(a1, a2));

            // when
            List<Auction> result = auctionRepository.findActive(
                    new GetAuctionItemStatsQuery(
                            RegionType.KR, 101L, 100L, null)
            );

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getPrice()).isLessThan(result.get(1).getPrice());
        }
    }

    @Nested
    @DisplayName("edge case")
    class EdgeCase {
        @Test
        @DisplayName("region, realmId가 일치하지 않으면 반환되지 않는다")
        void shouldReturnEmpty_whenRegionOrRealmIdMismatch() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.US, 101L, null);
            AuctionEntity a2 = createAuction(2L, 100L, 2, 500L, RegionType.KR, 102L, null);
            auctionJpaRepository.saveAll(List.of(a1, a2));

            // when
            List<Auction> result = auctionRepository.findActive(
                    new GetAuctionItemStatsQuery(
                            RegionType.KR, 101L, 100L, null)
            );

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("itemId가 일치하지 않으면 반환되지 않는다")
        void shouldReturnEmpty_whenItemIdMismatch() {
            // given
            AuctionEntity a1 = createAuction(1L, 200L, 2, 500L, RegionType.KR, 101L, null);
            auctionJpaRepository.save(a1);

            // when
            List<Auction> result = auctionRepository.findActive(
                    new GetAuctionItemStatsQuery(
                            RegionType.KR, 101L, 100L, null)
            );

            // then
            assertThat(result).isEmpty();
        }
    }
} 
