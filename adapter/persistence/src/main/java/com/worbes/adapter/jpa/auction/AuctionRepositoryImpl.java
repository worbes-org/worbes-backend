package com.worbes.adapter.jpa.auction;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SearchAuctionSummaryCondition;
import com.worbes.application.auction.port.out.AuctionReadRepository;
import com.worbes.application.auction.port.out.AuctionSummary;
import com.worbes.application.auction.port.out.AuctionTrend;
import com.worbes.application.auction.port.out.AuctionWriteRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements AuctionWriteRepository, AuctionReadRepository {

    private final AuctionEntityMapper auctionEntityMapper;
    private final AuctionTrendViewMapper statsSnapshotMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    @Override
    public int upsertAll(List<Auction> auctions) {
        String sql = """
                INSERT INTO auction (
                    auction_id,
                    item_id,
                    quantity,
                    price,
                    region,
                    realm_id,
                    created_at
                )
                VALUES (
                    :auctionId,
                    :itemId,
                    :quantity,
                    :price,
                    :region,
                    :realmId,
                    NOW()
                )
                ON CONFLICT (auction_id) DO UPDATE SET
                    quantity = EXCLUDED.quantity
                WHERE auction.quantity != EXCLUDED.quantity
                """;

        List<AuctionEntity> entities = auctions.stream()
                .map(auctionEntityMapper::toEntity)
                .toList();

        MapSqlParameterSource[] params = entities.stream()
                .map(auction -> new MapSqlParameterSource()
                        .addValue("auctionId", auction.getAuctionId())
                        .addValue("itemId", auction.getItemId())
                        .addValue("quantity", auction.getQuantity())
                        .addValue("price", auction.getPrice())
                        .addValue("region", auction.getRegion().name())
                        .addValue("realmId", auction.getRealmId()))
                .toArray(MapSqlParameterSource[]::new);

        int[] results = jdbcTemplate.batchUpdate(sql, params);
        return Arrays.stream(results).sum();
    }

    @Override
    public Long updateEndedAtBy(RegionType region, Long realmId, Set<Long> auctionIds) {
        QAuctionEntity a = QAuctionEntity.auctionEntity;
        BooleanExpression realmCondition = createRealmCondition(a, realmId);

        return queryFactory.update(a)
                .set(a.endedAt, Instant.now())
                .where(
                        a.region.eq(region),
                        realmCondition,
                        a.auctionId.in(auctionIds)
                )
                .execute();
    }

    @Override
    public List<AuctionSummary> findAllSummaryByCondition(SearchAuctionSummaryCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        RegionType region = condition.region();
        Long realmId = condition.realmId();
        Set<Long> itemIds = condition.itemIds();
        int pageSize = condition.pageInfo().pageSize();
        long offset = condition.pageInfo().offset();
        BooleanExpression realmCondition = createRealmCondition(auction, realmId);


        return queryFactory
                .select(Projections.constructor(
                        AuctionSummary.class,
                        auction.itemId,
                        auction.price.min(),
                        auction.quantity.sum()
                ))
                .from(auction)
                .where(
                        auction.region.eq(region),
                        realmCondition,
                        auction.itemId.in(itemIds),
                        auction.endedAt.isNull()
                )
                .groupBy(auction.itemId)
                .offset(offset)
                .limit(pageSize + 1)
                .fetch();
    }

    @Override
    public List<Auction> findAllActiveBy(Long itemId, RegionType region, Long realmId) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        BooleanExpression realmCondition = createRealmCondition(auction, realmId);

        List<AuctionEntity> entities = queryFactory
                .selectFrom(auction)
                .where(
                        auction.endedAt.isNull(),
                        auction.itemId.eq(itemId),
                        auction.region.eq(region),
                        realmCondition
                )
                .orderBy(auction.price.asc().nullsLast())
                .fetch();

        return entities.stream()
                .map(auctionEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<AuctionTrend> findHourlyTrendBy(Long itemId, RegionType region, Long realmId, Integer dayMinus) {
        QAuctionTrendView trendView = QAuctionTrendView.auctionTrendView;

        List<AuctionTrendView> entities = queryFactory
                .selectFrom(trendView)
                .where(
                        trendView.itemId.eq(itemId),
                        trendView.region.eq(region),
                        trendView.time.goe(LocalDateTime.now().minusDays(dayMinus)),
                        realmId == null ? trendView.realmId.isNull() : trendView.realmId.eq(realmId)
                )
                .orderBy(trendView.time.asc())
                .fetch();

        return entities.stream()
                .map(statsSnapshotMapper::toDomain)
                .toList();
    }

    private BooleanExpression createRealmCondition(QAuctionEntity auction, Long realmId) {
        if (realmId == null) {
            return auction.realmId.isNull();
        }
        return auction.realmId.eq(realmId).or(auction.realmId.isNull());
    }
}
