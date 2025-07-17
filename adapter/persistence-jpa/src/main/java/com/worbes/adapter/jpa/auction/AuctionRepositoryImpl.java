package com.worbes.adapter.jpa.auction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionItemStatsQuery;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionRepository {

    private final AuctionMapper auctionMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public Long updateEndedAt(RegionType region, Long realmId, Set<Long> auctionIds) {
        QAuctionEntity a = QAuctionEntity.auctionEntity;

        return queryFactory.update(a)
                .set(a.endedAt, Instant.now())
                .where(
                        a.region.eq(region),
                        a.id.in(auctionIds),
                        createRealmCondition(a, realmId)
                )
                .execute();
    }

    @Override
    public List<Auction> findActive(GetAuctionItemStatsQuery query) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        Long itemId = query.itemId();
        RegionType region = query.region();
        Long realmId = query.realmId();

        List<AuctionEntity> entities = queryFactory
                .selectFrom(auction)
                .where(
                        auction.endedAt.isNull(),
                        auction.itemId.eq(itemId),
                        auction.region.eq(region),
                        createRealmCondition(auction, realmId)
                )
                .orderBy(auction.price.asc().nullsLast())
                .fetch();

        return entities.stream()
                .map(auctionMapper::toDomain)
                .toList();
    }

    private BooleanExpression createRealmCondition(QAuctionEntity auction, Long realmId) {
        if (realmId == null) {
            return auction.realmId.isNull();
        }
        return auction.realmId.eq(realmId).or(auction.realmId.isNull());
    }
}
