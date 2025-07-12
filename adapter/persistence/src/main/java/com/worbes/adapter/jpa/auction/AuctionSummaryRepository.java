package com.worbes.adapter.jpa.auction;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.adapter.jpa.bonus.QAuctionBonusEntity;
import com.worbes.adapter.jpa.bonus.QItemBonusEntity;
import com.worbes.adapter.jpa.item.QItemEntity;
import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.out.AuctionSummaryQueryRepository;
import com.worbes.application.auction.port.out.AuctionSummarySearchCondition;
import com.worbes.application.item.model.Item;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AuctionSummaryRepository implements AuctionSummaryQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final AuctionSummaryProjectionMapper mapper;

    @Override
    public List<AuctionSummary> findSummary(AuctionSummarySearchCondition condition) {
        QAuctionEntity a = QAuctionEntity.auctionEntity;
        QAuctionBonusEntity ab = QAuctionBonusEntity.auctionBonusEntity;
        QItemBonusEntity b = QItemBonusEntity.itemBonusEntity;
        QItemEntity i = QItemEntity.itemEntity;

        int pageSize = condition.pageInfo().pageSize();
        long offset = condition.pageInfo().offset();
        Long realmId = condition.realmId();
        RegionType region = condition.region();
        Map<Long, Item> items = condition.items().stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        NumberTemplate<Integer> totalQuantity = Expressions.numberTemplate(
                Integer.class,
                "COALESCE(NULLIF(SUM(CASE WHEN {0} IS NULL THEN {1} END), 0), NULLIF(COUNT(DISTINCT CASE WHEN {0} IS NOT NULL THEN {2} END), 0))",
                a.realmId,
                a.quantity,
                a.id
        );

        return queryFactory
                .select(Projections.constructor(
                        AuctionSummaryProjection.class,
                        a.itemId,
                        a.price.min(),
                        totalQuantity,
                        a.itemBonus,
                        b.level.max(),
                        b.baseLevel.max()
                ))
                .from(a)
                .join(i).on(a.itemId.eq(i.id))
                .leftJoin(ab).on(ab.auctionId.eq(a.auctionId))
                .leftJoin(b).on(b.id.eq(ab.itemBonusId))
                .where(
                        a.region.eq(region),
                        a.endedAt.isNull(),
                        a.itemId.in(items.keySet()),
                        createRealmCondition(a, realmId),
                        b.baseLevel.coalesce(i.level).add(b.level.coalesce(0)).goe(0)
                )
                .groupBy(a.itemId, a.itemBonus)
                .orderBy(a.price.min().desc())
                .offset(offset)
                .limit(pageSize + 1)
                .fetch()
                .stream()
                .map(projection -> mapper.toDomain(projection, items.get(projection.itemId())))
                .toList();
    }

    private BooleanExpression createRealmCondition(QAuctionEntity auction, Long realmId) {
        if (realmId == null) {
            return auction.realmId.isNull();
        }
        return auction.realmId.eq(realmId).or(auction.realmId.isNull());
    }
}
