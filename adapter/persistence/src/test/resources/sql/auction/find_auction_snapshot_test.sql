-- ============================
-- 테스트 데이터 삽입
-- ============================

-- 1. realm
INSERT INTO realm (id, region, connected_realm_id, name, slug, created_at, updated_at)
VALUES (1, 'KR', 100, '{
  "ko_KR": "아즈샤라"
}', 'azshara', now(), now()),
       (2, 'US', 200, '{
         "en_US": "Stormrage"
       }', 'stormrage', now(), now());

-- 2. item
INSERT INTO item (id, name, level, class_id, subclass_id, inventory_type, quality, icon, display_id, crafting_tier,
                  is_stackable, expansion_id, created_at, updated_at)
VALUES (1001, '{
  "ko_KR": "테스트 검"
}', 200, 2, 7, 'TWOHWEAPON', 5, 'sword_icon.png', 5001, null, false, 5, now(), now()),
       (1002, '{
         "ko_KR": "테스트 반지"
       }', 120, 4, 0, 'FINGER', 3, 'ring_icon.png', 5002, null, false, 7, now(), now()),
       (1003, '{
         "ko_KR": "닻풀"
       }', 45, 4, 0, 'NON_EQUIP', 3, 'herb_icon.png', 5002, 1, true, 9, now(), now());

-- 3. item_bonus
INSERT INTO item_bonus (id, suffix, level, base_level, created_at, updated_at)
VALUES (9001, null, 5, null, now(), now()),
       (9002, null, 10, null, now(), now()),
       (9003, 'of the Quickblade', null, 230, now(), now());

-- 5. auction_snapshot
INSERT INTO auction_snapshot (item_id, time, realm_id, region, lowest_price, total_quantity, item_bonus)
VALUES (1001, now(), 1, 'KR', 150000, 2, '[
  9001,
  9003
]'::jsonb),
       (1001, now() - interval '1 hour', 1, 'KR', 170000, 1, '[
         9001,
         9003
       ]'::jsonb),
       (1002, now(), 1, 'KR', 50000, 10, '[
         9002
       ]'::jsonb),
       (1002, now() - interval '1 hour', 1, 'KR', 40000, 11, '[
         9002
       ]'::jsonb),
       (1003, now(), null, 'KR', 30000, 11035, null),
       (1003, now() - interval '1 hour', null, 'KR', 20000, 12266, null),
       (1001, now(), 2, 'US', 140000, 1, null);
