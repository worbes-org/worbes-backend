package com.worbes.adapter.jpa.auction;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.AuctionCommandRepository;
import com.worbes.application.auction.port.out.AuctionQueryRepository;
import com.worbes.application.auction.port.out.AuctionSearchCondition;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionRepository implements AuctionCommandRepository, AuctionQueryRepository {

    private final AuctionEntityMapper auctionEntityMapper;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    @Override
    public int upsertAll(List<Auction> auctions) {
        List<AuctionEntity> entities = auctions.stream()
                .map(auctionEntityMapper::toEntity)
                .toList();

        String sql = """
                INSERT INTO auction (
                    id,
                    item_id,
                    quantity,
                    price,
                    region,
                    realm_id,
                    created_at
                )
                VALUES (
                    :id,
                    :itemId,
                    :quantity,
                    :price,
                    :region,
                    :realmId,
                    NOW()
                )
                ON CONFLICT (id) DO UPDATE SET
                    quantity = EXCLUDED.quantity
                WHERE auction.quantity != EXCLUDED.quantity
                """;

        MapSqlParameterSource[] params = entities.stream()
                .map(entity -> new MapSqlParameterSource()
                        .addValue("id", entity.getId())
                        .addValue("itemId", entity.getItemId())
                        .addValue("quantity", entity.getQuantity())
                        .addValue("price", entity.getPrice())
                        .addValue("region", entity.getRegion().name())
                        .addValue("realmId", entity.getRealmId())
                )
                .toArray(MapSqlParameterSource[]::new);

        int[] results = jdbcTemplate.batchUpdate(sql, params);
        return Arrays.stream(results).sum();
    }

    @Override
    public Long updateEndedAtBy(RegionType region, Long realmId, Set<Long> auctionIds) {
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
    public List<Auction> findActive(AuctionSearchCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        Long itemId = condition.itemId();
        RegionType region = condition.region();
        Long realmId = condition.realmId();

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
                .map(auctionEntityMapper::toDomain)
                .toList();
    }

    private BooleanExpression createRealmCondition(QAuctionEntity auction, Long realmId) {
        if (realmId == null) {
            return auction.realmId.isNull();
        }
        return auction.realmId.eq(realmId).or(auction.realmId.isNull());
    }
}
