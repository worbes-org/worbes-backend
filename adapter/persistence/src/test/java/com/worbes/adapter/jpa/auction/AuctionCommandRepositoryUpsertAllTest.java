package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.AuctionCommandRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuctionCommandRepositoryUpsertAllTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 101L;

    @Autowired
    private AuctionCommandRepository auctionCommandRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    private Auction createAuction(Long auctionId, Long itemId, Integer quantity, Long price, RegionType region, Long realmId) {
        return Auction.builder()
                .id(auctionId)
                .itemId(itemId)
                .quantity(quantity)
                .price(price)
                .region(region)
                .realmId(realmId)
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("수량이 변경된 경우에만 update 된다")
        void upsert_updates_only_when_quantity_changed() {
            // given: 최초 저장
            Auction auction1 = createAuction(1001L, 1L, 10, 1000L, region, realmId);
            Auction auction2 = createAuction(1002L, 2L, 20, 2000L, region, realmId);
            auctionCommandRepository.upsertAll(List.of(auction1, auction2));

            // when: auction1의 quantity만 변경
            Auction auction1Changed = createAuction(1001L, 1L, 99, 1000L, region, realmId);
            Auction auction2Unchanged = createAuction(1002L, 2L, 20, 2000L, region, realmId);
            int updated = auctionCommandRepository.upsertAll(List.of(auction1Changed, auction2Unchanged));

            // then: auction1만 update, auction2는 update 없음
            List<AuctionEntity> all = auctionJpaRepository.findAll();
            AuctionEntity updatedAuction1 = all.stream().filter(a -> a.getAuctionId().equals(1001L)).findFirst().orElseThrow();
            AuctionEntity updatedAuction2 = all.stream().filter(a -> a.getAuctionId().equals(1002L)).findFirst().orElseThrow();
            assertThat(updated).isEqualTo(1);
            assertThat(updatedAuction1.getQuantity()).isEqualTo(99L);
            assertThat(updatedAuction2.getQuantity()).isEqualTo(20L);
        }

        @Test
        @DisplayName("변경 사항이 없으면 update가 발생하지 않는다")
        void upsert_no_update_when_no_change() {
            // given
            Auction auction = createAuction(2001L, 1L, 10, 1000L, region, realmId);
            auctionCommandRepository.upsertAll(List.of(auction));

            // when: 동일 데이터로 upsert
            int updated = auctionCommandRepository.upsertAll(List.of(auction));

            // then
            assertThat(updated).isZero();
            AuctionEntity entity = auctionJpaRepository.findAll().get(0);
            assertThat(entity.getQuantity()).isEqualTo(10L);
        }

        @Test
        @DisplayName("여러 건 중 일부만 변경된 경우 변경된 건만 update 된다")
        void upsert_partial_update() {
            // given
            Auction auction1 = createAuction(3001L, 1L, 10, 1000L, region, realmId);
            Auction auction2 = createAuction(3002L, 2L, 20, 2000L, region, realmId);
            Auction auction3 = createAuction(3003L, 3L, 30, 3000L, region, realmId);
            auctionCommandRepository.upsertAll(List.of(auction1, auction2, auction3));

            // when: auction2만 quantity 변경
            Auction auction2Changed = createAuction(3002L, 2L, 99, 2000L, region, realmId);
            int updated = auctionCommandRepository.upsertAll(List.of(auction1, auction2Changed, auction3));

            // then
            assertThat(updated).isEqualTo(1);
            AuctionEntity updatedAuction2 = auctionJpaRepository.findAll().stream()
                    .filter(a -> a.getAuctionId().equals(3002L)).findFirst().orElseThrow();
            assertThat(updatedAuction2.getQuantity()).isEqualTo(99L);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("빈 리스트를 넣으면 아무 일도 일어나지 않는다")
        void upsert_empty_list() {
            // when
            int updated = auctionCommandRepository.upsertAll(List.of());
            // then
            assertThat(updated).isZero();
            assertThat(auctionJpaRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("대량 데이터 입력 시 정상적으로 처리된다")
        void upsert_handles_large_batch() {
            // given
            int batchSize = 1000;
            List<Auction> auctions = IntStream.range(0, batchSize)
                    .mapToObj(i -> createAuction(10000L + i, 1L + i, 10, 1000L, region, realmId))
                    .toList();
            // when
            int updated = auctionCommandRepository.upsertAll(auctions);
            // then
            assertThat(updated).isEqualTo(batchSize);
            assertThat(auctionJpaRepository.findAll()).hasSize(batchSize);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("auctionId가 null이면 예외가 발생한다")
        void upsert_throws_exception_when_auctionId_is_null() {
            // given
            Auction badAuction = Auction.builder()
                    .id(null)
                    .itemId(1L)
                    .quantity(5)
                    .price(500L)
                    .region(region)
                    .realmId(realmId)
                    .build();
            // when, then
            assertThatThrownBy(() ->
                    auctionCommandRepository.upsertAll(List.of(badAuction))
            ).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("음수 수량을 저장하면 예외가 발생한다")
        void upsert_throws_exception_when_quantity_is_negative() {
            // given
            Auction badAuction = createAuction(9001L, 1L, -10, 1000L, region, realmId);
            // when, then
            assertThatThrownBy(() ->
                    auctionCommandRepository.upsertAll(List.of(badAuction))
            ).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("가격이 0 이하이면 예외가 발생한다")
        void upsert_throws_exception_when_price_is_zero_or_negative() {
            // given
            Auction zeroPrice = createAuction(9101L, 1L, 10, 0L, region, realmId);
            Auction negativePrice = createAuction(9102L, 1L, 10, -100L, region, realmId);
            // when, then
            assertThatThrownBy(() ->
                    auctionCommandRepository.upsertAll(List.of(zeroPrice))
            ).isInstanceOfAny(DataIntegrityViolationException.class, UncategorizedSQLException.class);
            assertThatThrownBy(() ->
                    auctionCommandRepository.upsertAll(List.of(negativePrice))
            ).isInstanceOfAny(DataIntegrityViolationException.class, UncategorizedSQLException.class);
        }
    }
} 
