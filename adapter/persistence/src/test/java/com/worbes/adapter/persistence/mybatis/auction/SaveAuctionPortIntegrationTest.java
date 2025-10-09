package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SaveAuctionPortIntegrationTest {

    @Autowired
    AuctionMybatisAdapter auctionMybatisAdapter;

    @Nested
    @DisplayName("Happy Case")
    class HappyCase {
        @Test
        @DisplayName("경매 데이터 리스트 정상 upsert 시, DB에 반영되고 반환값이 1이어야 한다.")
        void upsertAll_success() {
            List<Auction> auctions = List.of(
                    Auction.builder()
                            .id(1L)
                            .itemId(100L)
                            .quantity(5)
                            .unitPrice(1000L)
                            .region(RegionType.KR)
                            .build()
            );
            int result = auctionMybatisAdapter.saveAll(auctions);
            assertThat(result).isEqualTo(1);
        }

        @Test
        @DisplayName("여러 건의 경매 데이터 upsert 시, 모두 정상 반영되고 반환값이 N이어야 한다.")
        void upsertAll_multipleRows() {
            List<Auction> auctions = List.of(
                    Auction.builder()
                            .id(1L)
                            .itemId(100L)
                            .quantity(5)
                            .unitPrice(1000L)
                            .region(RegionType.KR)
                            .build(),
                    Auction.builder()
                            .id(2L)
                            .itemId(200L)
                            .quantity(3)
                            .unitPrice(2000L)
                            .region(RegionType.US)
                            .build(),
                    Auction.builder()
                            .id(3L)
                            .itemId(200L)
                            .realmId(100L)
                            .quantity(3)
                            .buyout(2000L)
                            .region(RegionType.US)
                            .build()
            );
            int result = auctionMybatisAdapter.saveAll(auctions);
            assertThat(result).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Edge Case")
    class EdgeCase {
        @Test
        @DisplayName("빈 리스트 입력 시, 0을 반환해야 한다.")
        void upsertAll_emptyList() {
            List<Auction> auctions = List.of();
            int result = auctionMybatisAdapter.saveAll(auctions);
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("itemBonus가 null 또는 빈 리스트여도 정상적으로 upsert")
        void upsertAll_itemBonusNullOrEmpty() {
            Auction auctionNull = Auction.builder()
                    .id(10L)
                    .itemId(110L)
                    .realmId(11L)
                    .quantity(2)
                    .buyout(1500L)
                    .region(RegionType.KR)
                    .itemBonus(null)
                    .build();
            Auction auctionEmpty = Auction.builder()
                    .id(11L)
                    .itemId(120L)
                    .realmId(12L)
                    .quantity(3)
                    .buyout(1600L)
                    .region(RegionType.KR)
                    .itemBonus(List.of())
                    .build();
            int result = auctionMybatisAdapter.saveAll(List.of(auctionNull, auctionEmpty));
            assertThat(result).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Fail Case")
    class FailCase {
        @Test
        @DisplayName("DB 제약조건 위반 시 예외가 발생해야 한다.")
        void upsertAll_constraintViolation() {
            List<Auction> auctions = List.of(
                    Auction.builder()
                            .id(1L)
                            .itemId(100L)
                            .realmId(10L)
                            .quantity(0) // quantity >= 1 제약 위반
                            .buyout(1000L)
                            .region(RegionType.KR)
                            .itemBonus(List.of(1L, 2L))
                            .build()
            );
            assertThatThrownBy(() -> auctionMybatisAdapter.saveAll(auctions))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
