-- ================================
-- V1_3__views.sql
-- 데이터베이스 뷰
-- ================================

-- ----------------------------------------------------------------------------
-- 1. Auction Snapshot with Item View
-- ----------------------------------------------------------------------------
-- 경매 스냅샷 + 아이템 보너스 정보 조인 뷰
-- item_bonus를 jsonb array에서 풀어서 최대값 계산

CREATE OR REPLACE VIEW auction_snapshot_with_item_view AS
SELECT ash.id,
       ash.item_id,
       ash.item_bonus,
       ash.lowest_price,
       ash.total_quantity,
       MAX(ib.level)      AS bonus_level,
       MAX(ib.base_level) AS base_level,
       MAX(ib.suffix)     AS suffix,
       ash.region,
       ash.realm_id,
       ash.time
FROM auction_snapshot ash
         LEFT JOIN LATERAL jsonb_array_elements_text(ash.item_bonus) AS bonus(bonus_id) ON true
         LEFT JOIN item_bonus ib ON ib.id = bonus.bonus_id::bigint
GROUP BY ash.id, ash.item_id, ash.item_bonus, ash.time, ash.region, ash.realm_id, ash.lowest_price, ash.total_quantity;
