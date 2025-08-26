package com.worbes.adapter.persistence.jpa.auction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.application.auction.port.out.DeleteAuctionPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionJpaAdapter implements DeleteAuctionPort {

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteAll(RegionType region, Long realmId) {
        QAuctionEntity a = QAuctionEntity.auctionEntity;

        BooleanExpression realmCondition = Optional.ofNullable(realmId)
                .map(a.realmId::eq)
                .orElseGet(a.realmId::isNull);

        return queryFactory.delete(a)
                .where(
                        a.region.eq(region),
                        realmCondition
                )
                .execute();
    }
}
