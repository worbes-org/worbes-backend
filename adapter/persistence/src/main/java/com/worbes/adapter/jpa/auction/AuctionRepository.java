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
                    auction_id,
                    item_id,
                    quantity,
                    price,
                    region,
                    realm_id,
                    created_at,
                    item_bonus
                )
                VALUES (
                    :auctionId,
                    :itemId,
                    :quantity,
                    :price,
                    :region,
                    :realmId,
                    NOW(),
                    :itemBonus
                )
                ON CONFLICT (auction_id) DO UPDATE SET
                    quantity = EXCLUDED.quantity
                WHERE auction.quantity != EXCLUDED.quantity
                """;

        MapSqlParameterSource[] params = entities.stream()
                .map(entity -> new MapSqlParameterSource()
                        .addValue("auctionId", entity.getAuctionId())
                        .addValue("itemId", entity.getItemId())
                        .addValue("quantity", entity.getQuantity())
                        .addValue("price", entity.getPrice())
                        .addValue("region", entity.getRegion().name())
                        .addValue("realmId", entity.getRealmId())
                        .addValue("itemBonus", entity.getItemBonus())
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
                        createRealmCondition(a, realmId),
                        a.auctionId.in(auctionIds)
                )
                .execute();
    }

    @Override
    public List<Auction> findActive(AuctionSearchCondition condition) {
        QAuctionEntity auction = QAuctionEntity.auctionEntity;
        Long itemId = condition.itemId();
        RegionType region = condition.region();
        Long realmId = condition.realmId();
        String itemBonus = condition.itemBonus();

        List<AuctionEntity> entities = queryFactory
                .selectFrom(auction)
                .where(
                        auction.endedAt.isNull(),
                        auction.itemId.eq(itemId),
                        auction.region.eq(region),
                        createRealmCondition(auction, realmId),
                        createItemBonusCondition(auction, itemBonus)
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

    private BooleanExpression createItemBonusCondition(QAuctionEntity auction, String itemBonus) {
        if (itemBonus == null || itemBonus.isEmpty()) {
            return auction.itemBonus.isNull();
        }
        return auction.itemBonus.eq(itemBonus);
    }
}
