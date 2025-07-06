package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.AuctionReadRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuctionReadRepositoryFindAllActiveByTest {

    @Autowired
    private AuctionReadRepository auctionRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    private static AuctionEntity createAuction(Long auctionId, Long itemId, int quantity, long price, RegionType region, Long realmId, java.time.Instant endedAt) {
        return AuctionEntity.builder()
                .auctionId(auctionId)
                .itemId(itemId)
                .quantity(quantity)
                .price(price)
                .region(region)
                .realmId(realmId)
                .endedAt(endedAt)
                .build();
    }

    @Nested
    @DisplayName("정상 동작")
    class HappyCases {
        @Test
        @DisplayName("endedAt이 null인 경매만 반환된다")
        void onlyActiveAuctionsReturned() {
            // given
            AuctionEntity active1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L, null);
            AuctionEntity ended = createAuction(2L, 100L, 1, 600L, RegionType.KR, 101L, java.time.Instant.now());
            auctionJpaRepository.saveAll(List.of(active1, ended));

            // when
            List<Auction> result = auctionRepository.findAllActiveBy(100L, RegionType.KR, 101L);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("여러 active 경매가 가격 오름차순으로 반환된다")
        void multipleActiveSortedByPrice() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 700L, RegionType.KR, 101L, null);
            AuctionEntity a2 = createAuction(2L, 100L, 2, 500L, RegionType.KR, 101L, null);
            auctionJpaRepository.saveAll(List.of(a1, a2));

            // when
            List<Auction> result = auctionRepository.findAllActiveBy(100L, RegionType.KR, 101L);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getPrice()).isLessThan(result.get(1).getPrice());
        }
    }

    @Nested
    @DisplayName("경계/실패 케이스")
    class EdgeAndFailCases {
        @Test
        @DisplayName("region, realmId가 일치하지 않으면 반환되지 않는다")
        void regionOrRealmMismatch() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.US, 101L, null);
            AuctionEntity a2 = createAuction(2L, 100L, 2, 500L, RegionType.KR, 102L, null);
            auctionJpaRepository.saveAll(List.of(a1, a2));

            // when
            List<Auction> result = auctionRepository.findAllActiveBy(100L, RegionType.KR, 101L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("itemId가 일치하지 않으면 반환되지 않는다")
        void itemIdMismatch() {
            // given
            AuctionEntity a1 = createAuction(1L, 200L, 2, 500L, RegionType.KR, 101L, null);
            auctionJpaRepository.save(a1);

            // when
            List<Auction> result = auctionRepository.findAllActiveBy(100L, RegionType.KR, 101L);

            // then
            assertThat(result).isEmpty();
        }
    }
} 
