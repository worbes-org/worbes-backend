package com.worbes.adapter.jpa.repository;

import com.worbes.application.auction.model.AuctionStatsSnapshot;
import com.worbes.application.auction.model.Price;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Integration::SearchAuctionRepository::findHourlySnapshots")
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class FindHourlyAuctionSnapshotTest {

    @Autowired
    private SearchAuctionRepository searchAuctionRepository;

    /**
     * 기대 결과 예시
     * hour	total_quantity	min_price
     * now-4h  10	1000
     * now-3h  15	900
     * now-2h  23	900
     * now-1h  18	850
     * <p>
     * 10시: 10개(1000원)
     * 11시: 10+5=15개(900원)
     * 12시: 10+5+8=23개(900원, 950원은 더 비쌈)
     * 13시: 10+5+3=18개(12시에 생성된 8개는 ended_at이 13시라서 13시에는 빠짐, 최저가 850원)
     */
    @Test
    @Sql(scripts = "find_hourly_auction_snapshot_test.sql")
    @DisplayName("findHourlySnapshots는 시간별 스냅샷을 올바르게 반환한다")
    void findHourlySnapshotsTest() {
        // given
        Long itemId = 1L;
        RegionType region = RegionType.KR;
        Long realmId = 101L;
        Integer dayMinus = 7;

        // when
        List<AuctionStatsSnapshot> snapshots = searchAuctionRepository.findHourlySnapshots(itemId, region, realmId, dayMinus);

        // then
        assertThat(snapshots).hasSize(4);
        assertThat(snapshots.get(0).getTotalQuantity()).isEqualTo(10L);
        assertThat(snapshots.get(0).getLowestPrice().equals(new Price(1000L))).isTrue();
        assertThat(snapshots.get(1).getTotalQuantity()).isEqualTo(15L);
        assertThat(snapshots.get(1).getLowestPrice().equals(new Price(900L))).isTrue();
        assertThat(snapshots.get(2).getTotalQuantity()).isEqualTo(23L);
        assertThat(snapshots.get(2).getLowestPrice().equals(new Price(900L))).isTrue();
        assertThat(snapshots.get(3).getTotalQuantity()).isEqualTo(18L);
        assertThat(snapshots.get(3).getLowestPrice().equals(new Price(850L))).isTrue();
    }

    @Sql(scripts = "test-some-auctions-ended.sql")
    @Test
    @DisplayName("일부 경매가 종료된 경우, 종료된 경매는 집계에서 제외된다")
    void testSomeAuctionsEnded() {
        Long itemId = 1L;
        RegionType region = RegionType.KR;
        Long realmId = 101L;
        Integer dayMinus = 7;
        List<AuctionStatsSnapshot> snapshots = searchAuctionRepository.findHourlySnapshots(itemId, region, realmId, dayMinus);

        for (AuctionStatsSnapshot s : snapshots) {
            System.out.println("time=" + s.getTime() + ", totalQuantity=" + s.getTotalQuantity() + ", minPrice=" + s.getLowestPrice());
        }

        // 예시: 마지막 스냅샷만 검증하지 말고, 전체를 검증
        assertThat(snapshots).isNotEmpty();
        // 예시: 각 시점별 기대값을 명확히 비교
        assertThat(snapshots.get(0).getTotalQuantity()).isEqualTo(10L);
        assertThat(snapshots.get(1).getTotalQuantity()).isEqualTo(15L);
        assertThat(snapshots.get(2).getTotalQuantity()).isEqualTo(23L);
        assertThat(snapshots.get(3).getTotalQuantity()).isEqualTo(18L);
    }

    @Sql(scripts = "test-different-realmid.sql")
    @Test
    @DisplayName("realmId가 다르면 각각 따로 집계된다")
    void testDifferentRealmId() {
        Long itemId = 1L;
        RegionType region = RegionType.KR;
        Integer dayMinus = 7;
        List<AuctionStatsSnapshot> snapshots101 = searchAuctionRepository.findHourlySnapshots(itemId, region, 101L, dayMinus);
        List<AuctionStatsSnapshot> snapshots102 = searchAuctionRepository.findHourlySnapshots(itemId, region, 102L, dayMinus);
        assertThat(snapshots101.get(0).getTotalQuantity()).isEqualTo(10L);
        assertThat(snapshots102.get(0).getTotalQuantity()).isEqualTo(5L);
    }

    @Sql(scripts = "test-different-itemid.sql")
    @Test
    @DisplayName("itemId가 다르면 각각 따로 집계된다")
    void testDifferentItemId() {
        RegionType region = RegionType.KR;
        Long realmId = 101L;
        Integer dayMinus = 7;
        List<AuctionStatsSnapshot> snapshots1 = searchAuctionRepository.findHourlySnapshots(1L, region, realmId, dayMinus);
        List<AuctionStatsSnapshot> snapshots2 = searchAuctionRepository.findHourlySnapshots(2L, region, realmId, dayMinus);
        assertThat(snapshots1.get(0).getTotalQuantity()).isEqualTo(10L);
        assertThat(snapshots2.get(0).getTotalQuantity()).isEqualTo(5L);
    }

    @Sql(scripts = "test-all-active.sql")
    @Test
    @DisplayName("ended_at이 전부 null이면 모두 활성 상태로 집계된다")
    void testAllActive() {
        Long itemId = 1L;
        RegionType region = RegionType.KR;
        Long realmId = 101L;
        Integer dayMinus = 7;
        List<AuctionStatsSnapshot> snapshots = searchAuctionRepository.findHourlySnapshots(itemId, region, realmId, dayMinus);
        assertThat(snapshots).isNotEmpty();
        assertThat(snapshots.get(snapshots.size() - 1).getTotalQuantity()).isEqualTo(15L);
    }

    @Sql(scripts = "test-dayminus-exceeded.sql")
    @Test
    @DisplayName("dayMinus 파라미터 초과 기간의 경매는 집계에 포함되지 않는다")
    void testDayMinusExceeded() {
        Long itemId = 1L;
        RegionType region = RegionType.KR;
        Long realmId = 101L;
        Integer dayMinus = 7;
        List<AuctionStatsSnapshot> snapshots = searchAuctionRepository.findHourlySnapshots(itemId, region, realmId, dayMinus);
        assertThat(snapshots).isEmpty();
    }
}
