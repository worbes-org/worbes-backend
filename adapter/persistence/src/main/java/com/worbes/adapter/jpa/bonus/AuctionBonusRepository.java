package com.worbes.adapter.jpa.bonus;

import com.worbes.application.bonus.port.model.AuctionBonus;
import com.worbes.application.bonus.port.out.AuctionBonusCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionBonusRepository implements AuctionBonusCommandRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void saveAllIgnoreConflict(List<AuctionBonus> auctionBonuses) {
        List<AuctionBonusEntity> entities = auctionBonuses.stream()
                .map(ab -> new AuctionBonusEntity(ab.auctionId(), ab.itemBonusId()))
                .toList();

        String sql = """
                INSERT INTO auction_bonus (auction_id, item_bonus_id)
                VALUES (:auctionId, :itemBonusId)
                ON CONFLICT (auction_id, item_bonus_id) DO NOTHING
                """;

        List<MapSqlParameterSource> batchParams = entities.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("auctionId", e.getAuctionId())
                        .addValue("itemBonusId", e.getItemBonusId()))
                .toList();

        namedParameterJdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
    }
}
