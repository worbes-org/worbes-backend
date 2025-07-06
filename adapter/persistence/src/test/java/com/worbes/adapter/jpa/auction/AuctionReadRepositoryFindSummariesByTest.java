package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.port.in.SearchAuctionSummaryCondition;
import com.worbes.application.auction.port.out.AuctionReadRepository;
import com.worbes.application.auction.port.out.AuctionSummary;
import com.worbes.application.common.model.PageInfo;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuctionReadRepositoryFindSummariesByTest {

    @Autowired
    private AuctionReadRepository auctionRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    // 테스트용 경매 엔티티 생성 메서드
    private static AuctionEntity createAuction(Long auctionId, Long itemId, int quantity, long price, RegionType region, Long realmId) {
        return AuctionEntity.builder()
                .auctionId(auctionId)
                .itemId(itemId)
                .quantity(quantity)
                .price(price)
                .region(region)
                .realmId(realmId)
                .endedAt(null)
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("여러 itemId, region, realmId 조합에 대해 요약 결과가 올바르게 반환된다")
        void givenValidItemsAndRegionAndRealm_whenFindSummaries_thenReturnsCorrectSummaries() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            AuctionEntity a2 = createAuction(2L, 100L, 3, 400L, RegionType.KR, 101L);
            AuctionEntity a3 = createAuction(3L, 200L, 1, 1000L, RegionType.KR, 101L);
            AuctionEntity a4 = createAuction(4L, 100L, 1, 300L, RegionType.KR, 101L); // 최저가
            AuctionEntity a5 = createAuction(5L, 100L, 1, 200L, RegionType.US, 101L); // region 다름
            AuctionEntity a6 = createAuction(6L, 100L, 1, 100L, RegionType.KR, 102L); // realm 다름
            AuctionEntity a7 = createAuction(7L, 300L, 5, 700L, RegionType.KR, 101L);
            auctionJpaRepository.saveAll(List.of(a1, a2, a3, a4, a5, a6, a7));

            SearchAuctionSummaryCondition query = new SearchAuctionSummaryCondition(
                    RegionType.KR,
                    101L,
                    Set.of(100L, 200L, 300L),
                    new PageInfo(0L, 10)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findAllSummaryByCondition(query);

            // then
            assertThat(summaries).hasSize(3);
            AuctionSummary summary100 = summaries.stream().filter(s -> s.itemId().equals(100L)).findFirst().orElseThrow();
            assertThat(summary100.lowestPrice()).isEqualTo(300L);
            assertThat(summary100.available()).isEqualTo(6); // 2+3+1

            AuctionSummary summary200 = summaries.stream().filter(s -> s.itemId().equals(200L)).findFirst().orElseThrow();
            assertThat(summary200.lowestPrice()).isEqualTo(1000L);
            assertThat(summary200.available()).isEqualTo(1);

            AuctionSummary summary300 = summaries.stream().filter(s -> s.itemId().equals(300L)).findFirst().orElseThrow();
            assertThat(summary300.lowestPrice()).isEqualTo(700L);
            assertThat(summary300.available()).isEqualTo(5);
        }

        @Test
        @DisplayName("페이지네이션: pageSize=2, offset=0이면 최대 3개(pageSize+1) 반환된다")
        void pagination_pageSize2_offset0_returns3() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            AuctionEntity a2 = createAuction(2L, 200L, 1, 1000L, RegionType.KR, 101L);
            AuctionEntity a3 = createAuction(3L, 300L, 5, 700L, RegionType.KR, 101L);
            auctionJpaRepository.saveAll(List.of(a1, a2, a3));

            SearchAuctionSummaryCondition query = new SearchAuctionSummaryCondition(
                    RegionType.KR,
                    101L,
                    Set.of(100L, 200L, 300L),
                    new PageInfo(0L, 2)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findAllSummaryByCondition(query);

            // then
            assertThat(summaries).hasSize(3); // pageSize+1
        }

        @Test
        @DisplayName("페이지네이션: pageSize=2, offset=2이면 최대 2개 반환된다")
        void pagination_pageSize2_offset2_returns1or2() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            AuctionEntity a2 = createAuction(2L, 200L, 1, 1000L, RegionType.KR, 101L);
            AuctionEntity a3 = createAuction(3L, 300L, 5, 700L, RegionType.KR, 101L);
            auctionJpaRepository.saveAll(List.of(a1, a2, a3));

            SearchAuctionSummaryCondition query = new SearchAuctionSummaryCondition(
                    RegionType.KR,
                    101L,
                    Set.of(100L, 200L, 300L),
                    new PageInfo(2L, 2)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findAllSummaryByCondition(query);

            // then
            // 실제 남은 데이터가 2개 이하이므로, 1~2개 반환
            assertThat(summaries.size()).isBetween(1, 2);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {

        @Test
        @DisplayName("offset이 데이터 개수 이상이면 빈 리스트 반환")
        void pagination_offsetOver_returnsEmpty() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            auctionJpaRepository.save(a1);
            SearchAuctionSummaryCondition query = new SearchAuctionSummaryCondition(
                    RegionType.KR,
                    101L,
                    Set.of(100L),
                    new PageInfo(10L, 2)
            );
            // when
            List<AuctionSummary> summaries = auctionRepository.findAllSummaryByCondition(query);
            // then
            assertThat(summaries).isEmpty();
        }

        @Test
        @DisplayName("대량 데이터(100개) 페이지네이션 정상 동작(pageSize+1)")
        void pagination_largeData() {
            // given
            for (long i = 1; i <= 100; i++) {
                auctionJpaRepository.save(createAuction(i, 100L + i, 1, 1000L + i, RegionType.KR, 101L));
            }
            SearchAuctionSummaryCondition query = new SearchAuctionSummaryCondition(
                    RegionType.KR,
                    101L,
                    // 100개 itemId
                    LongStream.rangeClosed(101L, 200L).boxed().collect(Collectors.toSet()),
                    new PageInfo(10L, 10)
            );
            // when
            List<AuctionSummary> summaries = auctionRepository.findAllSummaryByCondition(query);
            // then
            assertThat(summaries).hasSize(11); // pageSize+1
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("조건에 맞는 경매가 없으면 빈 리스트를 반환한다")
        void givenNoMatchingAuctions_whenFindSummaries_thenReturnsEmptyList() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.US, 101L);
            auctionJpaRepository.save(a1);

            SearchAuctionSummaryCondition query = new SearchAuctionSummaryCondition(
                    RegionType.KR,
                    101L,
                    Set.of(200L),
                    new PageInfo(0L, 10)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findAllSummaryByCondition(query);

            // then
            assertThat(summaries).isEmpty();
        }
    }
} 
