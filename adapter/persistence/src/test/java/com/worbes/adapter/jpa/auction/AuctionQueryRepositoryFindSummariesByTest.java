package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.out.AuctionSummaryQueryRepository;
import com.worbes.application.auction.port.out.AuctionSummarySearchCondition;
import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.item.port.out.ItemCommandRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuctionQueryRepositoryFindSummariesByTest {

    @Autowired
    private AuctionSummaryQueryRepository auctionRepository;

    @Autowired
    private ItemCommandRepository itemCommandRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    // 테스트용 경매 엔티티 생성 메서드
    private static AuctionEntity createAuction(Long auctionId, Long itemId, int quantity, long price, RegionType region, Long realmId) {
        AuctionEntity entity = AuctionEntity.builder()
                .auctionId(auctionId)
                .itemId(itemId)
                .quantity(quantity)
                .price(price)
                .region(region)
                .realmId(realmId)
                .build();
        entity.setEndedAt(null);
        return entity;
    }

    @Nested
    @DisplayName("happy case")
    class HappyCase {
        @Test
        @DisplayName("여러 itemId, region, realmId 조합에 대해 요약 결과가 올바르게 반환된다")
        void shouldReturnCorrectSummaries_whenValidItemsAndRegionAndRealm() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 1, 500L, RegionType.KR, 101L);
            AuctionEntity a2 = createAuction(2L, 100L, 1, 400L, RegionType.KR, 101L);
            AuctionEntity a3 = createAuction(3L, 200L, 1, 1000L, RegionType.KR, 101L);
            AuctionEntity a4 = createAuction(4L, 100L, 1, 300L, RegionType.KR, 101L); // 최저가
            AuctionEntity a5 = createAuction(5L, 100L, 1, 200L, RegionType.US, 101L); // region 다름
            AuctionEntity a6 = createAuction(6L, 100L, 1, 100L, RegionType.KR, 102L); // realm 다름
            AuctionEntity a7 = createAuction(7L, 300L, 1, 700L, RegionType.KR, 101L);
            auctionJpaRepository.saveAll(List.of(a1, a2, a3, a4, a5, a6, a7));

            List<Item> items = List.of(
                    Item.builder().id(100L).name(Map.of("ko_KR", "item100"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon100.png").isStackable(false).build(),
                    Item.builder().id(200L).name(Map.of("ko_KR", "item200"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon200.png").isStackable(false).build(),
                    Item.builder().id(300L).name(Map.of("ko_KR", "item300"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon300.png").isStackable(false).build()
            );
            itemCommandRepository.saveAll(items);
            AuctionSummarySearchCondition query = new AuctionSummarySearchCondition(
                    RegionType.KR,
                    101L,
                    items,
                    new PageInfo(0L, 10)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findSummary(query);

            // then
            assertThat(summaries).hasSize(3);
            AuctionSummary summary100 = summaries.stream().filter(s -> s.getItem().getId().equals(100L)).findFirst().orElseThrow();
            assertThat(summary100.getLowestPrice()).isEqualTo(300L);
            assertThat(summary100.getAvailable()).isEqualTo(3); // row 개수

            AuctionSummary summary200 = summaries.stream().filter(s -> s.getItem().getId().equals(200L)).findFirst().orElseThrow();
            assertThat(summary200.getLowestPrice()).isEqualTo(1000L);
            assertThat(summary200.getAvailable()).isEqualTo(1);

            AuctionSummary summary300 = summaries.stream().filter(s -> s.getItem().getId().equals(300L)).findFirst().orElseThrow();
            assertThat(summary300.getLowestPrice()).isEqualTo(700L);
            assertThat(summary300.getAvailable()).isEqualTo(1);
        }

        @Test
        @DisplayName("페이지네이션: pageSize=2, offset=0이면 최대 3개(pageSize+1) 반환된다")
        void shouldReturnPageSizePlusOne_whenPageSize2Offset0() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            AuctionEntity a2 = createAuction(2L, 200L, 1, 1000L, RegionType.KR, 101L);
            AuctionEntity a3 = createAuction(3L, 300L, 5, 700L, RegionType.KR, 101L);
            auctionJpaRepository.saveAll(List.of(a1, a2, a3));

            List<Item> items = List.of(
                    Item.builder().id(100L).name(Map.of("ko_KR", "item100"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon100.png").isStackable(true).build(),
                    Item.builder().id(200L).name(Map.of("ko_KR", "item200"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon200.png").isStackable(true).build(),
                    Item.builder().id(300L).name(Map.of("ko_KR", "item300"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon300.png").isStackable(true).build()
            );
            itemCommandRepository.saveAll(items);
            AuctionSummarySearchCondition query = new AuctionSummarySearchCondition(
                    RegionType.KR,
                    101L,
                    items,
                    new PageInfo(0L, 2)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findSummary(query);

            // then
            assertThat(summaries).hasSize(3); // pageSize+1
        }

        @Test
        @DisplayName("페이지네이션: pageSize=2, offset=2이면 최대 2개 반환된다")
        void shouldReturnOneOrTwo_whenPageSize2Offset2() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            AuctionEntity a2 = createAuction(2L, 200L, 1, 1000L, RegionType.KR, 101L);
            AuctionEntity a3 = createAuction(3L, 300L, 5, 700L, RegionType.KR, 101L);
            auctionJpaRepository.saveAll(List.of(a1, a2, a3));

            List<Item> items = List.of(
                    Item.builder().id(100L).name(Map.of("ko_KR", "item100"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon100.png").isStackable(true).build(),
                    Item.builder().id(200L).name(Map.of("ko_KR", "item200"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon200.png").isStackable(true).build(),
                    Item.builder().id(300L).name(Map.of("ko_KR", "item300"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon300.png").isStackable(true).build()
            );
            itemCommandRepository.saveAll(items);
            AuctionSummarySearchCondition query = new AuctionSummarySearchCondition(
                    RegionType.KR,
                    101L,
                    items,
                    new PageInfo(2L, 2)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findSummary(query);

            // then
            // 실제 남은 데이터가 2개 이하이므로, 1~2개 반환
            assertThat(summaries.size()).isBetween(1, 2);
        }
    }

    @Nested
    @DisplayName("edge case")
    class EdgeCase {

        @Test
        @DisplayName("offset이 데이터 개수 이상이면 빈 리스트 반환")
        void shouldReturnEmpty_whenOffsetOverDataCount() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.KR, 101L);
            auctionJpaRepository.save(a1);
            List<Item> items = List.of(
                    Item.builder().id(100L).name(Map.of("ko_KR", "item100"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon100.png").isStackable(true).build()
            );
            itemCommandRepository.saveAll(items);
            AuctionSummarySearchCondition query = new AuctionSummarySearchCondition(
                    RegionType.KR,
                    101L,
                    items,
                    new PageInfo(10L, 2)
            );
            // when
            List<AuctionSummary> summaries = auctionRepository.findSummary(query);
            // then
            assertThat(summaries).isEmpty();
        }

        @Test
        @DisplayName("대량 데이터(100개) 페이지네이션 정상 동작(pageSize+1)")
        void shouldReturnPageSizePlusOne_whenLargeData() {
            // given
            for (long i = 1; i <= 100; i++) {
                auctionJpaRepository.save(createAuction(i, 100L + i, 1, 1000L + i, RegionType.KR, 101L));
            }
            List<Item> items = LongStream.rangeClosed(101L, 200L)
                    .mapToObj(id -> Item.builder().id(id).name(Map.of("ko_KR", "item" + id))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon" + id + ".png").isStackable(true).build())
                    .toList();
            itemCommandRepository.saveAll(items);
            AuctionSummarySearchCondition query = new AuctionSummarySearchCondition(
                    RegionType.KR,
                    101L,
                    items,
                    new PageInfo(10L, 10)
            );
            // when
            List<AuctionSummary> summaries = auctionRepository.findSummary(query);
            // then
            assertThat(summaries).hasSize(11); // pageSize+1
        }
    }

    @Nested
    @DisplayName("fail case")
    class FailCase {
        @Test
        @DisplayName("조건에 맞는 경매가 없으면 빈 리스트를 반환한다")
        void shouldReturnEmpty_whenNoMatchingAuctions() {
            // given
            AuctionEntity a1 = createAuction(1L, 100L, 2, 500L, RegionType.US, 101L);
            auctionJpaRepository.save(a1);

            List<Item> items = List.of(
                    Item.builder().id(200L).name(Map.of("ko_KR", "item200"))
                            .classId(1L).subclassId(1L).quality(QualityType.RARE).level(10)
                            .inventoryType(InventoryType.WEAPON).icon("icon200.png").isStackable(true).build()
            );
            itemCommandRepository.saveAll(items);
            AuctionSummarySearchCondition query = new AuctionSummarySearchCondition(
                    RegionType.KR,
                    101L,
                    items,
                    new PageInfo(0L, 10)
            );

            // when
            List<AuctionSummary> summaries = auctionRepository.findSummary(query);

            // then
            assertThat(summaries).isEmpty();
        }
    }
} 
