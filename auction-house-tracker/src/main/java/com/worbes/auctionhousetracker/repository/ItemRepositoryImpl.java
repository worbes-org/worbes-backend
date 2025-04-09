package com.worbes.auctionhousetracker.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemCustomRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void batchInsertIgnoreConflicts(List<Item> items) {
        if (items.isEmpty()) return;

        String sql = """
                INSERT INTO item (
                    id,
                    name,
                    item_class_id,
                    item_subclass_id,
                    quality,
                    level,
                    inventory_type,
                    preview_item,
                    icon_url,
                    created_at,
                    updated_at
                ) VALUES (?, ?::jsonb, ?, ?, ?, ?, ?, ?::jsonb, ?, now(), now())
                ON CONFLICT (id) DO NOTHING
                """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Item item = items.get(i);
                ps.setLong(1, item.getId());
                ps.setString(2, toJson(item.getName()));
                ps.setLong(3, item.getItemClass().getId());
                ps.setLong(4, item.getItemSubclass().getId());
                ps.setString(5, item.getQuality().name());
                ps.setInt(6, item.getLevel());
                ps.setString(7, item.getInventoryType().name());
                ps.setString(8, toJson(item.getPreviewItem()));
                ps.setString(9, item.getIconUrl());
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("🔥 JSON 직렬화 실패: " + e.getMessage(), e);
        }

    }
}
