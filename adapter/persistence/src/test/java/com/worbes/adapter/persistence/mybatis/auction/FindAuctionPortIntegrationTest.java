package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionDetailQuery;
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
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FindAuctionPortIntegrationTest {

    Long itemId = 123L;
    RegionType region = RegionType.KR;
    Long realmId = 101L;
    List<Long> expectedBonus = List.of(100L, 101L);

    @Autowired
    AuctionMybatisAdapter auctionMybatisAdapter;

    @Nested
    @DisplayName("Happy Case")
    class HappyCase {
        @Test
        @DisplayName("정확히 일치하는 경우 - 포함됨")
        void findBy_exactMatch_included() {
            Auction auction = Auction.builder()
                    .id(1L)
                    .itemId(itemId)
                    .region(region)
                    .realmId(realmId)
                    .quantity(1)
                    .buyout(1000L)
                    .itemBonus(expectedBonus)
                    .build();

            auctionMybatisAdapter.saveAll(List.of(auction));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, itemId, expectedBonus)
            );

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("itemBonus 순서 달라도 포함됨")
        void findBy_itemBonusDifferentOrder_included() {
            Auction auction = Auction.builder()
                    .id(4L)
                    .itemId(itemId)
                    .region(region)
                    .realmId(realmId)
                    .quantity(1)
                    .buyout(1000L)
                    .itemBonus(List.of(101L, 100L)) // 순서 다름
                    .build();

            auctionMybatisAdapter.saveAll(List.of(auction));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, itemId, expectedBonus)
            );

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(4L);
        }

        @Test
        @DisplayName("여러 아이템 중 일치하는 itemId만 반환됨")
        void findBy_multipleItems_onlyMatchingItemReturned() {
            Long targetItemId = 1000L;
            Long otherItemId = 2000L;

            Auction match = Auction.builder()
                    .id(9L)
                    .itemId(targetItemId)
                    .region(region)
                    .realmId(realmId)
                    .quantity(1)
                    .buyout(1000L)
                    .itemBonus(expectedBonus)
                    .build();

            Auction notMatch = Auction.builder()
                    .id(10L)
                    .itemId(otherItemId)
                    .region(region)
                    .realmId(realmId)
                    .quantity(1)
                    .buyout(500L)
                    .itemBonus(expectedBonus)  // bonus는 같지만 itemId가 다름
                    .build();

            auctionMybatisAdapter.saveAll(List.of(match, notMatch));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, targetItemId, expectedBonus)
            );

            assertThat(result)
                    .hasSize(1)
                    .extracting(Auction::getId)
                    .containsExactly(9L);
        }
    }

    @Nested
    @DisplayName("Edge Case")
    class EdgeCase {
        @Test
        @DisplayName("itemBonus에 초과 항목이 있으면 제외됨")
        void findBy_itemBonusExtra_excluded() {
            Auction auction = Auction.builder()
                    .id(2L)
                    .itemId(itemId)
                    .region(region)
                    .realmId(realmId)
                    .quantity(1)
                    .buyout(1000L)
                    .itemBonus(List.of(100L, 101L, 102L)) // 초과
                    .build();

            auctionMybatisAdapter.saveAll(List.of(auction));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, itemId, expectedBonus)
            );

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("itemBonus가 일부만 일치하면 제외됨")
        void findBy_itemBonusPartialMatch_excluded() {
            Auction auction = Auction.builder()
                    .id(3L)
                    .itemId(itemId)
                    .region(region)
                    .realmId(realmId)
                    .quantity(1)
                    .buyout(1000L)
                    .itemBonus(List.of(100L)) // 일부만 포함
                    .build();

            auctionMybatisAdapter.saveAll(List.of(auction));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, itemId, expectedBonus)
            );

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("realm_id가 null이어도 포함됨")
        void findBy_realmIdNull_matchesWithGivenRealmId() {
            Auction auction = Auction.builder()
                    .id(5L)
                    .itemId(itemId)
                    .region(region)
                    .quantity(1)
                    .unitPrice(1000L)
                    .build();

            auctionMybatisAdapter.saveAll(List.of(auction));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, itemId, null)
            );

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("Fail Case")
    class FailCase {
        @Test
        @DisplayName("realmId가 다른 경우 조회되지 않음")
        void findBy_realmMismatch_shouldNotMatch() {
            Auction auction = Auction.builder()
                    .id(13L)
                    .itemId(itemId)
                    .region(region)
                    .realmId(999L)
                    .quantity(1)
                    .buyout(1000L)
                    .itemBonus(expectedBonus)
                    .build();

            auctionMybatisAdapter.saveAll(List.of(auction));

            List<Auction> result = auctionMybatisAdapter.findBy(
                    new GetAuctionDetailQuery(region, realmId, itemId, expectedBonus)
            );

            assertThat(result).isEmpty();
        }
    }
}
