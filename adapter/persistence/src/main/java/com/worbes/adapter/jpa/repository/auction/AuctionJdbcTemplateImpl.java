package com.worbes.adapter.jpa.repository.auction;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionJdbcTemplateImpl implements AuctionJdbcTemplate {

    private static final String SQL_INSERT_IGNORE_CONFLICT = """
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

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int saveAllIgnoreConflict(List<AuctionEntity> auctions) {
        MapSqlParameterSource[] params = auctions.stream()
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

        int[] results = jdbcTemplate.batchUpdate(SQL_INSERT_IGNORE_CONFLICT, params);
        return Arrays.stream(results).sum();
    }
}
