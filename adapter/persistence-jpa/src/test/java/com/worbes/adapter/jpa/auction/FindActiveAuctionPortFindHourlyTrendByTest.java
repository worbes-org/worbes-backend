package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.out.AuctionTrendQueryRepository;
import com.worbes.application.auction.port.out.AuctionTrendSearchCondition;
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
class FindActiveAuctionPortFindHourlyTrendByTest {

    @Autowired
    private AuctionTrendQueryRepository auctionTrendQueryRepository;

    @Nested
    @DisplayName("happy case")
    class HappyCase {
        @Test
        @Sql(scripts = "hourly_trend_happy_case.sql")
        @DisplayName("시간별 트렌드를 올바르게 반환한다")
        void shouldReturnCorrectHourlyTrends() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrendPoint> trends = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, realmId, itemId, null, dayMinus)
            );
            assertThat(trends).hasSize(4);
            assertThat(trends.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(trends.get(0).lowestPrice()).isEqualTo(1000L);
            assertThat(trends.get(1).totalQuantity()).isEqualTo(15L);
            assertThat(trends.get(1).lowestPrice()).isEqualTo(900L);
            assertThat(trends.get(2).totalQuantity()).isEqualTo(23L);
            assertThat(trends.get(2).lowestPrice()).isEqualTo(900L);
            assertThat(trends.get(3).totalQuantity()).isEqualTo(18L);
            assertThat(trends.get(3).lowestPrice()).isEqualTo(850L);
        }

        @Test
        @Sql(scripts = "hourly_trend_test-all-active.sql")
        @DisplayName("ended_at이 전부 null이면 모두 활성 상태로 집계된다")
        void shouldAggregateAllAsActive_whenAllEndedAtIsNull() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrendPoint> snapshots = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, realmId, itemId, null, dayMinus)
            );
            assertThat(snapshots).isNotEmpty();
            assertThat(snapshots.get(snapshots.size() - 1).totalQuantity()).isEqualTo(15L);
        }
    }

    @Nested
    @DisplayName("edge case")
    class EdgeCase {
        @Test
        @Sql(scripts = "hourly_trend_test-different-realmid.sql")
        @DisplayName("realmId가 다르면 각각 따로 집계된다")
        void shouldAggregateSeparately_whenRealmIdIsDifferent() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Integer dayMinus = 7;
            List<AuctionTrendPoint> snapshots101 = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, 101L, itemId, null, dayMinus)
            );
            List<AuctionTrendPoint> snapshots102 = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, 102L, itemId, null, dayMinus)
            );
            assertThat(snapshots101.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(snapshots102.get(0).totalQuantity()).isEqualTo(5L);
        }

        @Test
        @Sql(scripts = "hourly_trend_test-different-itemid.sql")
        @DisplayName("itemId가 다르면 각각 따로 집계된다")
        void shouldAggregateSeparately_whenItemIdIsDifferent() {
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrendPoint> snapshots1 = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, realmId, 1L, null, dayMinus)
            );
            List<AuctionTrendPoint> snapshots2 = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, realmId, 2L, null, dayMinus)
            );
            assertThat(snapshots1.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(snapshots2.get(0).totalQuantity()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("fail case")
    class FailCase {
        @Test
        @Sql(scripts = "hourly_trend_test-dayminus-exceeded.sql")
        @DisplayName("dayMinus 파라미터 초과 기간의 경매는 집계에 포함되지 않는다")
        void shouldNotIncludeAuctions_whenDayMinusExceeded() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrendPoint> snapshots = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, realmId, itemId, null, dayMinus)
            );
            assertThat(snapshots).isEmpty();
        }

        @Test
        @Sql(scripts = "hourly_trend_test-some-auctions-ended.sql")
        @DisplayName("일부 경매가 종료된 경우, 종료된 경매는 집계에서 제외된다")
        void shouldExcludeEndedAuctions_whenSomeAuctionsEnded() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrendPoint> snapshots = auctionTrendQueryRepository.findHourlyTrend(
                    new AuctionTrendSearchCondition(region, realmId, itemId, null, dayMinus)
            );
            assertThat(snapshots).isNotEmpty();
            assertThat(snapshots.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(snapshots.get(1).totalQuantity()).isEqualTo(15L);
            assertThat(snapshots.get(2).totalQuantity()).isEqualTo(23L);
            assertThat(snapshots.get(3).totalQuantity()).isEqualTo(18L);
        }
    }
}
