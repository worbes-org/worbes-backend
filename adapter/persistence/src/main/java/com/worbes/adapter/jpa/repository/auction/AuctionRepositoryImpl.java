package com.worbes.adapter.jpa.repository.auction;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.adapter.jpa.entity.QAuctionEntity;
import com.worbes.adapter.jpa.mapper.AuctionEntityMapper;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.out.CreateAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.auction.port.out.UpdateAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements CreateAuctionRepository, UpdateAuctionRepository, SearchAuctionRepository {

    private final AuctionJdbcTemplate auctionJdbcTemplate;
    private final AuctionEntityMapper auctionEntityMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public int saveAllIgnoreConflict(List<Auction> auctions) {
        List<AuctionEntity> entities = auctions.stream()
                .map(auctionEntityMapper::toEntity)
                .toList();

        return auctionJdbcTemplate.saveAllIgnoreConflict(entities);
    }

    @Override
    public Long deactivateBy(RegionType region, Long realmId, Set<Long> auctionIds) {
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
                        a.auctionId.in(auctionIds)
                )
                .execute();
    }

    @Override
    public List<SearchAuctionSummaryResult> searchSummaries(SearchAuctionCommand command, Set<Long> itemIds) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        int pageSize = command.pageSize();

        Coalesce<Long> minPrice = new Coalesce<>(Long.class)
                .add(auction.unitPrice.min())
                .add(auction.buyout.min());

        return queryFactory
                .select(Projections.constructor(
                        SearchAuctionSummaryResult.class,
                        auction.itemId,
                        minPrice,
                        auction.quantity.sum()
                ))
                .from(auction)
                .where(
                        auction.region.eq(command.region()),
                        auction.realmId.eq(command.realmId()),
                        auction.itemId.in(itemIds),
                        auction.active.isTrue()
                )
                .groupBy(auction.itemId)
                .offset(command.offset())
                .limit(pageSize + 1)
                .fetch();
    }
}
