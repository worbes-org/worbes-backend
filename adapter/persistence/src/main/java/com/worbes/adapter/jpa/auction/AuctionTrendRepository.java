package com.worbes.adapter.jpa.auction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.out.AuctionTrendQueryRepository;
import com.worbes.application.auction.port.out.AuctionTrendSearchCondition;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionTrendRepository implements AuctionTrendQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final AuctionTrendViewMapper mapper;

    @Override
    public List<AuctionTrendPoint> findHourlyTrend(AuctionTrendSearchCondition condition) {
        QAuctionTrendView trendView = QAuctionTrendView.auctionTrendView;
        Long itemId = condition.itemId();
        Long realmId = condition.realmId();
        RegionType region = condition.region();
        Integer daysAgo = condition.daysAgo();
        String itemBonus = condition.itemBonus();

        List<AuctionTrendView> entities = queryFactory
                .selectFrom(trendView)
                .where(
                        trendView.itemId.eq(itemId),
                        trendView.region.eq(region),
                        trendView.time.goe(Instant.now().minus(daysAgo, ChronoUnit.DAYS)),
                        createRealmCondition(trendView, realmId),
                        createItemBonusCondition(trendView, itemBonus)
                )
                .orderBy(trendView.time.asc())
                .fetch();

        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    private BooleanExpression createRealmCondition(QAuctionTrendView trendView, Long realmId) {
        if (realmId == null) {
            return trendView.realmId.isNull();
        }
        return trendView.realmId.eq(realmId).or(trendView.realmId.isNull());
    }

    private BooleanExpression createItemBonusCondition(QAuctionTrendView trendView, String itemBonus) {
        if (itemBonus == null || itemBonus.isEmpty()) {
            return trendView.itemBonus.isNull();
        }
        return trendView.itemBonus.eq(itemBonus);
    }
}
