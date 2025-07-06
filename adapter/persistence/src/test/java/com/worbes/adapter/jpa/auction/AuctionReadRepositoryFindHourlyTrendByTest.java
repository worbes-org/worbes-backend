package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.port.out.AuctionReadRepository;
import com.worbes.application.auction.port.out.AuctionTrend;
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
class AuctionReadRepositoryFindHourlyTrendByTest {

    @Autowired
    private AuctionReadRepository auctionReadRepository;

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @Sql(scripts = "hourly_trend_happy_case.sql")
        @DisplayName("시간별 트렌드를 올바르게 반환한다")
        void findHourlyTrend_returnsCorrectTrends() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrend> trends = auctionReadRepository.findHourlyTrendBy(itemId, region, realmId, dayMinus);
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
        void allActive() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrend> snapshots = auctionReadRepository.findHourlyTrendBy(itemId, region, realmId, dayMinus);
            assertThat(snapshots).isNotEmpty();
            assertThat(snapshots.get(snapshots.size() - 1).totalQuantity()).isEqualTo(15L);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @Sql(scripts = "hourly_trend_test-different-realmid.sql")
        @DisplayName("realmId가 다르면 각각 따로 집계된다")
        void differentRealmId() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Integer dayMinus = 7;
            List<AuctionTrend> snapshots101 = auctionReadRepository.findHourlyTrendBy(itemId, region, 101L, dayMinus);
            List<AuctionTrend> snapshots102 = auctionReadRepository.findHourlyTrendBy(itemId, region, 102L, dayMinus);
            assertThat(snapshots101.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(snapshots102.get(0).totalQuantity()).isEqualTo(5L);
        }

        @Test
        @Sql(scripts = "hourly_trend_test-different-itemid.sql")
        @DisplayName("itemId가 다르면 각각 따로 집계된다")
        void differentItemId() {
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrend> snapshots1 = auctionReadRepository.findHourlyTrendBy(1L, region, realmId, dayMinus);
            List<AuctionTrend> snapshots2 = auctionReadRepository.findHourlyTrendBy(2L, region, realmId, dayMinus);
            assertThat(snapshots1.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(snapshots2.get(0).totalQuantity()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @Sql(scripts = "hourly_trend_test-dayminus-exceeded.sql")
        @DisplayName("dayMinus 파라미터 초과 기간의 경매는 집계에 포함되지 않는다")
        void dayMinusExceeded() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrend> snapshots = auctionReadRepository.findHourlyTrendBy(itemId, region, realmId, dayMinus);
            assertThat(snapshots).isEmpty();
        }

        @Test
        @Sql(scripts = "hourly_trend_test-some-auctions-ended.sql")
        @DisplayName("일부 경매가 종료된 경우, 종료된 경매는 집계에서 제외된다")
        void someAuctionsEnded() {
            Long itemId = 1L;
            RegionType region = RegionType.KR;
            Long realmId = 101L;
            Integer dayMinus = 7;
            List<AuctionTrend> snapshots = auctionReadRepository.findHourlyTrendBy(itemId, region, realmId, dayMinus);
            assertThat(snapshots).isNotEmpty();
            assertThat(snapshots.get(0).totalQuantity()).isEqualTo(10L);
            assertThat(snapshots.get(1).totalQuantity()).isEqualTo(15L);
            assertThat(snapshots.get(2).totalQuantity()).isEqualTo(23L);
            assertThat(snapshots.get(3).totalQuantity()).isEqualTo(18L);
        }
    }
}
