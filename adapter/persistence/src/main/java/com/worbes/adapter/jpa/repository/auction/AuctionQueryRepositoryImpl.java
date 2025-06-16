package com.worbes.adapter.jpa.repository.auction;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.adapter.jpa.entity.QAuctionEntity;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class AuctionQueryRepositoryImpl implements AuctionQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Long> findAuctionIdsByRegionAndRealmIdAndActiveTrue(RegionType region, Long realmId, Pageable pageable) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;

        BooleanBuilder builder = new BooleanBuilder()
                .and(auction.region.eq(region))
                .and(auction.active.isTrue());

        if (realmId == null) {
            builder.and(auction.realmId.isNull());
        } else {
            builder.and(auction.realmId.eq(realmId));
        }

        List<Long> content = queryFactory
                .select(auction.auctionId)
                .from(auction)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(auction.count())
                .from(auction)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Long deactivateByRegionAndRealmAndAuctionIdsIn(RegionType region, Long realmId, Set<Long> ids) {
        QAuctionEntity a = QAuctionEntity.auctionEntity;

        BooleanExpression realmCondition = (realmId != null)
                ? a.realmId.eq(realmId)
                : a.realmId.isNull();

        return queryFactory.update(a)
                .set(a.active, false)
                .set(a.updatedAt, LocalDateTime.now())
                .where(
                        a.region.eq(region),
                        realmCondition,
                        a.auctionId.in(ids)
                )
                .execute();
    }
}
