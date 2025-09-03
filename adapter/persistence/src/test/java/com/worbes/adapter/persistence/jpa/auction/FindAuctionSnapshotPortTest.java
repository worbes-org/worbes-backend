package com.worbes.adapter.persistence.jpa.auction;

import com.worbes.adapter.persistence.jpa.item.ItemEntity;
import com.worbes.adapter.persistence.jpa.item.ItemJpaRepository;
import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.SearchAuctionSummaryQuery;
import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/sql/auction/find_auction_snapshot_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class FindAuctionSnapshotPortTest {

    @Autowired
    AuctionSnapshotJpaAdapter auctionSnapshotJpaAdapter;

    @Autowired
    ItemJpaRepository itemJpaRepository;

    private List<Item> testItems;

    @BeforeEach
    void setUp() {
        testItems = itemJpaRepository.findAll().stream()
                .map(ItemEntity::toDomain)
                .toList();
    }

    @Test
    @DisplayName("정상 조회 - 조건에 맞는 데이터 정상 반환")
    void find_By_ReturnsCorrectData() {
        // given
        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                0,
                999,
                new PageInfo(0L, 10)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results)
                .allSatisfy(snapshot -> assertThat(testItems)
                        .extracting(Item::getId)
                        .contains(snapshot.getItemId()));
        assertThat(results)
                .allMatch(snapshot -> snapshot.getRegion().equals(RegionType.KR));
        assertThat(results)
                .allMatch(snapshot -> snapshot.getRealmId() == null || snapshot.getRealmId().equals(1L));
    }

    @Test
    @DisplayName("Item Level 범위 조건 - 경계 포함, 범위 밖 아이템 필터링 정상 동작")
    void itemLevelRange_FilterWorksCorrectly() {
        // given 테스트용 검 아이템 레벨 = 200, base = 230, bonus = 5
        int minLevel = 150;
        int maxLevel = 250;

        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                minLevel,
                maxLevel,
                new PageInfo(0L, 10)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();

        // 아이템 레벨 범위 조건 만족 확인
        assertThat(results).allMatch(snapshot -> {
            int effectiveLevel = snapshot.getItemLevel();
            return effectiveLevel >= minLevel && effectiveLevel <= maxLevel;
        });
    }

    @Test
    @DisplayName("Item Level 범위 조건 - minLevel=null인 경우 maxLevel만 적용")
    void itemLevelRange_MaxOnly_WorksCorrectly() {
        // given
        Integer maxLevel = 150; // 하위 아이템만 남아야 함

        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                null,
                maxLevel,
                new PageInfo(0L, 20)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(snapshot -> snapshot.getItemLevel() <= maxLevel);
    }

    @Test
    @DisplayName("Item Level 범위 조건 - maxLevel=null인 경우 minLevel만 적용")
    void itemLevelRange_MinOnly_WorksCorrectly() {
        // given
        Integer minLevel = 150; // 상위 아이템만 남아야 함

        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                minLevel,
                null,
                new PageInfo(0L, 20)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(snapshot -> snapshot.getItemLevel() >= minLevel);
    }

    @Test
    @DisplayName("Item Level 범위 조건 - minLevel, maxLevel 모두 null인 경우 조건 미적용")
    void itemLevelRange_BothNull_NoFilterApplied() {
        // given
        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                null,
                null,
                new PageInfo(0L, 20)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();
        // 전체 아이템이 다 나와야 함 → test data 기준으로 검증
        assertThat(results).hasSize(testItems.size());
    }

    @Test
    @DisplayName("RealmId=1 검색 시 글로벌(null realmId) 품목도 함께 조회되는지 확인")
    void findBy_WhenRealmIdIs1_IncludesGlobalAuctions() {
        // given
        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L, // realmId 지정
                testItems,
                0,
                999,
                new PageInfo(0L, 20)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();

        // realmId가 1 또는 null만 나와야 함
        assertThat(results)
                .allSatisfy(snapshot ->
                        assertThat(snapshot.getRealmId()).satisfies(
                                realmId -> assertThat(realmId == null || realmId.equals(1L)).isTrue()
                        )
                );

        // 글로벌 아이템(1003)이 포함되는지 확인
        assertThat(results).anySatisfy(snapshot ->
                assertThat(snapshot.getItemId()).isEqualTo(1003L)
        );
    }

    @Test
    @DisplayName("최신 시간 조건 - 가장 최신 시간의 데이터만 조회되는지 확인")
    void latestTimeCondition_OnlyLatestReturned() {
        // given
        SearchAuctionSummaryQuery query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                0,
                999,
                new PageInfo(0L, 20)
        );

        // when
        List<AuctionSnapshot> results = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(results).isNotEmpty();

        // 모든 결과가 동일한 최신 시간인지 확인
        assertThat(results.stream()
                .map(AuctionSnapshot::getTime)
                .distinct()
                .count()
        ).isEqualTo(1L);
    }

    @Test
    @DisplayName("페이징 동작 검증 - offset, limit에 따른 결과 개수 및 순서 확인")
    void find_By_PagingWorksCorrectly() {
        // given: offset 0, pageSize 2
        var query = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                0,
                999,
                new PageInfo(0L, 2)
        );

        // when
        var firstPage = auctionSnapshotJpaAdapter.findBy(query);

        // then
        assertThat(firstPage)
                .hasSizeLessThanOrEqualTo(3) // pageSize+1=3 (더보기 확인용)
                .hasSizeGreaterThan(0);
        assertThat(firstPage.stream().map(AuctionSnapshot::getItemId))
                .isSortedAccordingTo(Comparator.reverseOrder());

        // given: offset 2, pageSize 2 (다음 페이지)
        var queryNext = new SearchAuctionSummaryQuery(
                RegionType.KR,
                1L,
                testItems,
                0,
                999,
                new PageInfo(2L, 2)
        );

        // when
        var secondPage = auctionSnapshotJpaAdapter.findBy(queryNext);

        // then
        assertThat(secondPage)
                .hasSizeLessThanOrEqualTo(3)
                .allSatisfy(snapshot -> assertThat(testItems)
                        .extracting(Item::getId)
                        .contains(snapshot.getItemId()));
    }
}
