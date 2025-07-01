package com.worbes.adapter.jpa.repository.auction;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
        RegionType region = command.region();
        Long realmId = command.realmId();

        return queryFactory
                .select(Projections.constructor(
                        SearchAuctionSummaryResult.class,
                        auction.itemId,
                        auction.unitPrice.min(),
                        auction.buyout.min(),
                        auction.quantity.sum()
                ))
                .from(auction)
                .where(
                        auction.region.eq(region),
                        auction.realmId.eq(realmId),
                        auction.itemId.in(itemIds),
                        auction.active.isTrue()
                )
                .groupBy(auction.itemId)
                .fetch();
    }
}
