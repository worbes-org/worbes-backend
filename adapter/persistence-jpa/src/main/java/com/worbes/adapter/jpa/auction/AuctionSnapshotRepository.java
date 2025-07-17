package com.worbes.adapter.jpa.auction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.adapter.jpa.item.QItemEntity;
import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.GetAuctionItemTrendQuery;
import com.worbes.application.auction.port.in.SearchAuctionItemQuery;
import com.worbes.application.auction.port.out.FindAuctionSnapshotHistoryPort;
import com.worbes.application.auction.port.out.FindLatestAuctionSnapshotPort;
import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionSnapshotRepository implements FindAuctionSnapshotHistoryPort, FindLatestAuctionSnapshotPort {

    private final AuctionSnapshotMapper mapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<AuctionSnapshot> findLatest(SearchAuctionItemQuery query) {
        Set<Long> itemIds = query.items().stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
        Long realmId = query.realmId();
        RegionType region = query.region();
        long offset = query.pageInfo().offset();
        int pagedSize = query.pageInfo().pageSize();

        QAuctionSnapshotEntity ash = QAuctionSnapshotEntity.auctionSnapshotEntity;
        QItemEntity item = QItemEntity.itemEntity;

        JPQLQuery<Instant> maxTimeSubquery = JPAExpressions
                .select(ash.time.max())
                .from(ash)
                .where(
                        ash.realmId.isNull().or(ash.realmId.eq(realmId)),
                        ash.region.eq(region)
                );
        List<AuctionSnapshotEntity> fetched = queryFactory.selectFrom(ash)
                .join(item).on(ash.item.id.eq(item.id)).fetchJoin()
                .where(
                        ash.item.id.in(itemIds),
                        ash.realmId.isNull().or(ash.realmId.eq(realmId)),
                        ash.region.eq(region),
                        ash.time.eq(maxTimeSubquery)
                )
                .offset(offset)
                .limit(pagedSize + 1)
                .fetch();

        return fetched.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AuctionSnapshot> findHistory(GetAuctionItemTrendQuery query) {
        Long itemId = query.itemId();
        Long realmId = query.realmId();
        RegionType region = query.region();
        String itemBonus = query.itemBonus();
        Integer daysAgo = query.daysAgo();

        QAuctionSnapshotEntity ash = QAuctionSnapshotEntity.auctionSnapshotEntity;
        QItemEntity item = QItemEntity.itemEntity;
        BooleanExpression bonusExpression;

        if (itemBonus == null || itemBonus.isEmpty()) {
            bonusExpression = ash.bonusList.isNull();
        } else {
            bonusExpression = ash.bonusList.eq(itemBonus);
        }

        List<AuctionSnapshotEntity> fetched = queryFactory.selectFrom(ash)
                .join(item).on(ash.item.id.eq(item.id)).fetchJoin()
                .where(
                        ash.item.id.eq(itemId),
                        ash.realmId.isNull().or(ash.realmId.eq(realmId)),
                        ash.region.eq(region),
                        bonusExpression,
                        ash.time.goe(Instant.now().minus(daysAgo, ChronoUnit.DAYS))
                )
                .fetch();

        return fetched.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
