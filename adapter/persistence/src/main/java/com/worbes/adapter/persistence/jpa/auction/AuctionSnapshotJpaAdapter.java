package com.worbes.adapter.persistence.jpa.auction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.adapter.persistence.jpa.item.QItemEntity;
import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.SearchAuctionSummaryQuery;
import com.worbes.application.auction.port.out.DeleteAuctionSnapshotPort;
import com.worbes.application.auction.port.out.FindAuctionSnapshotPort;
import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionSnapshotJpaAdapter implements FindAuctionSnapshotPort, DeleteAuctionSnapshotPort {

    private static final int MIN_ITEM_LEVEL = 1;
    private static final int MAX_ITEM_LEVEL = 999;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<AuctionSnapshot> findBy(SearchAuctionSummaryQuery query) {
        Set<Long> itemIds = query.items().stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
        Long realmId = query.realmId();
        RegionType region = query.region();
        int minItemLevel = Optional.ofNullable(query.minItemLevel()).orElse(MIN_ITEM_LEVEL);
        int maxItemLevel = Optional.ofNullable(query.maxItemLevel()).orElse(MAX_ITEM_LEVEL);
        long offset = query.pageInfo().offset();
        int pagedSize = query.pageInfo().pageSize();

        QAuctionSnapshotEntity qAuctionSnapshotEntity = QAuctionSnapshotEntity.auctionSnapshotEntity;
        QAuctionSnapshotWithItemView view = QAuctionSnapshotWithItemView.auctionSnapshotWithItemView;
        QItemEntity item = QItemEntity.itemEntity;

        JPQLQuery<Instant> timeConditionSubquery = JPAExpressions
                .select(qAuctionSnapshotEntity.time.max())
                .from(qAuctionSnapshotEntity)
                .where(
                        qAuctionSnapshotEntity.realmId.eq(realmId),
                        qAuctionSnapshotEntity.region.eq(region)
                );

        BooleanExpression itemLevelCondition = new CaseBuilder()
                .when(view.baseLevel.isNotNull())
                .then(view.baseLevel.add(view.bonusLevel.coalesce(0)))
                .otherwise(item.level.add(view.bonusLevel.coalesce(0)))
                .between(minItemLevel, maxItemLevel);

        List<AuctionSnapshotWithItemView> fetched = queryFactory.selectFrom(view)
                .join(view.item, item).fetchJoin()
                .where(
                        view.item.id.in(itemIds),
                        view.realmId.isNull().or(view.realmId.eq(realmId)),
                        view.region.eq(region),
                        view.time.eq(timeConditionSubquery),
                        itemLevelCondition
                )
                .orderBy(view.item.id.desc())
                .offset(offset)
                .limit(pagedSize + 1)
                .fetch();

        return fetched.stream()
                .map(AuctionSnapshotWithItemView::toDomain)
                .toList();
    }

    /**
     * 한 달(30일) 이전 snapshot 데이터를 삭제합니다.
     *
     * @return 삭제된 행 수
     */
    public long deleteOlderThanOneMonth() {
        final long DAYS_30 = 30L * 24 * 60 * 60; // 30일을 초 단위로 상수화
        Instant cutoff = Instant.now().minusSeconds(DAYS_30);

        QAuctionSnapshotEntity auctionSnapshot = QAuctionSnapshotEntity.auctionSnapshotEntity;

        return queryFactory
                .delete(auctionSnapshot)
                .where(auctionSnapshot.time.lt(cutoff))
                .execute();
    }
}
