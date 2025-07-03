package com.worbes.adapter.jpa.repository;

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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements CreateAuctionRepository, UpdateAuctionRepository, SearchAuctionRepository {

    private final AuctionEntityMapper mapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    @Override
    public int saveAllIgnoreConflict(List<Auction> auctions) {
        String sql = """
                INSERT INTO auction (
                    auction_id,
                    item_id,
                    quantity,
                    unit_price,
                    buyout,
                    active,
                    region,
                    realm_id,
                    created_at,
                    updated_at
                )
                VALUES (
                    :auctionId,
                    :itemId,
                    :quantity,
                    :unitPrice,
                    :buyout,
                    :active,
                    :region,
                    :realmId,
                    NOW(),
                    NOW()
                )
                ON CONFLICT (auction_id) DO NOTHING
                """;

        List<AuctionEntity> entities = auctions.stream()
                .map(mapper::toEntity)
                .toList();

        MapSqlParameterSource[] params = entities.stream()
                .map(auction -> new MapSqlParameterSource()
                        .addValue("auctionId", auction.getAuctionId())
                        .addValue("itemId", auction.getItemId())
                        .addValue("quantity", auction.getQuantity())
                        .addValue("unitPrice", auction.getUnitPrice())
                        .addValue("buyout", auction.getBuyout())
                        .addValue("active", auction.isActive())
                        .addValue("region", auction.getRegion().name())
                        .addValue("realmId", auction.getRealmId()))
                .toArray(MapSqlParameterSource[]::new);

        int[] results = jdbcTemplate.batchUpdate(sql, params);
        return Arrays.stream(results).sum();
    }

    @Override
    public Long deactivate(RegionType region, Long realmId, Set<Long> auctionIds) {
        QAuctionEntity a = QAuctionEntity.auctionEntity;
        BooleanExpression realmCondition = getRealmCondition(a, realmId);

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
        Long realmId = command.realmId();
        Coalesce<Long> minPrice = coalescePrice(auction);
        BooleanExpression realmCondition = getRealmCondition(auction, realmId);

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
                        realmCondition,
                        auction.itemId.in(itemIds),
                        auction.active.isTrue()
                )
                .groupBy(auction.itemId)
                .offset(command.offset())
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<Auction> findActiveAuctions(Long itemId, RegionType region, Long realmId) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        Coalesce<Long> price = coalescePrice(auction);
        BooleanExpression realmCondition = getRealmCondition(auction, realmId);

        List<AuctionEntity> entities = queryFactory
                .selectFrom(auction)
                .where(
                        auction.active.isTrue(),
                        auction.itemId.eq(itemId),
                        auction.region.eq(region),
                        realmCondition
                )
                .orderBy(price.asc().nullsLast())
                .fetch();

        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    private Coalesce<Long> coalescePrice(QAuctionEntity auction) {
        return new Coalesce<>(Long.class)
                .add(auction.unitPrice)
                .add(auction.buyout);
    }

    private BooleanExpression getRealmCondition(QAuctionEntity auction, Long realmId) {
        return (realmId != null)
                ? auction.realmId.eq(realmId)
                : auction.realmId.isNull();
    }
}
