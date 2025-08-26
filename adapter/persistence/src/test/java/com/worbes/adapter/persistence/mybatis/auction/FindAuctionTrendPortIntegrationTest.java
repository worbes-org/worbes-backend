package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetAuctionTrendQuery;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/sql/auction/find_auction_trend_port_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class FindAuctionTrendPortIntegrationTest {

    private static final long ITEM_ID = 1234;
    private static final long REALM_ID = 205;
    private static final List<Long> ITEM_BONUS = List.of(100L, 101L);
    @Autowired
    AuctionSnapshotMybatisAdapter adapter;

    @Test
    @DisplayName("정확히 일치하는 snapshot 3건")
    void exactMatchSnapshot() {
        var query = new GetAuctionTrendQuery(RegionType.KR, REALM_ID, ITEM_ID, ITEM_BONUS, 3);
        List<AuctionTrendPoint> trends = adapter.findTrendsBy(query);

        assertThat(trends).hasSize(3);

        // id, itemId, itemBonus, time, lowestPrice, totalQuantity 검증
        trends.forEach(trend -> {
            assertThat(trend.itemId()).isEqualTo(ITEM_ID);
            assertThat(trend.itemBonus()).containsExactlyInAnyOrderElementsOf(ITEM_BONUS);
            assertThat(trend.time()).isNotNull();
            assertThat(trend.lowestPrice()).isNotNull();
            assertThat(trend.totalQuantity()).isNotNull();
        });
    }

    @Test
    @DisplayName("daysAgo 범위 초과 데이터 제외")
    void excludeOldData() {
        var query = new GetAuctionTrendQuery(RegionType.KR, REALM_ID, ITEM_ID, ITEM_BONUS, 3);
        List<AuctionTrendPoint> trends = adapter.findTrendsBy(query);

        assertThat(trends).noneMatch(trend -> trend.lowestPrice() == 14000L);
    }

    @Test
    @DisplayName("itemId 불일치")
    void excludeMismatchedItemId() {
        var query = new GetAuctionTrendQuery(RegionType.KR, REALM_ID, ITEM_ID, ITEM_BONUS, 3);
        List<AuctionTrendPoint> trends = adapter.findTrendsBy(query);

        assertThat(trends).noneMatch(trend -> trend.itemId() != ITEM_ID);
    }

    @Test
    @DisplayName("realmId 불일치")
    void excludeMismatchedRealmId() {
        var query = new GetAuctionTrendQuery(RegionType.KR, REALM_ID, ITEM_ID, ITEM_BONUS, 3);
        List<AuctionTrendPoint> trends = adapter.findTrendsBy(query);

        assertThat(trends).allMatch(trend -> trend.id() != 9999L); // realmId 9999인 row 제외
    }

    @Test
    @DisplayName("itemBonus 불일치 및 null 제외")
    void excludeInvalidOrNullItemBonus() {
        var query = new GetAuctionTrendQuery(RegionType.KR, REALM_ID, ITEM_ID, ITEM_BONUS, 3);
        List<AuctionTrendPoint> trends = adapter.findTrendsBy(query);

        trends.forEach(trend -> {
            assertThat(trend.itemBonus()).isNotNull();
            assertThat(trend.itemBonus()).containsExactlyInAnyOrderElementsOf(ITEM_BONUS);
        });
    }

    @Test
    @DisplayName("region 불일치")
    void excludeDifferentRegion() {
        var query = new GetAuctionTrendQuery(RegionType.KR, REALM_ID, ITEM_ID, ITEM_BONUS, 3);
        List<AuctionTrendPoint> trends = adapter.findTrendsBy(query);

        trends.forEach(trend -> assertThat(trend.id()).isNotEqualTo(17000L)); // US region row 제외
    }
}
