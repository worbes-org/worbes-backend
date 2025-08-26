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
         "ko_KR": "닻풀"
       }', 45, 4, 0, 'NON_EQUIP', 3, 'herb_icon.png', null, 1, true, 9, now(), now());

-- 3. item_bonus
INSERT INTO item_bonus (id, suffix, level, base_level, created_at, updated_at)
VALUES (9001, null, 5, null, now(), now()),
       (9002, null, 10, null, now(), now()),
       (9003, 'of the Quickblade', null, 230, now(), now());

INSERT INTO auction (id, item_id, item_bonus, price, quantity, region, realm_id, created_at)
VALUES (2001, 1001, '[
  9001,
  9003
]'::jsonb, 2000000, 1, 'KR', 1, now()),
       (2002, 1001, '[
         9001,
         9003
       ]'::jsonb, 2000500, 1, 'KR', 1, now()),
       (2003, 1002, null, 5000, 350, 'KR', null, now()),
       (2004, 1002, null, 6000, 150, 'KR', null, now());

-- 5. auction_snapshot
INSERT INTO auction_snapshot (item_id, time, realm_id, region, lowest_price, total_quantity, item_bonus)
VALUES (1001, now(), 1, 'KR', 2000000, 2, '[
  9001,
  9003
]'::jsonb),
       (1001, now() - interval '1 hour', 1, 'KR', 3000000, 2, '[
         9001,
         9003
       ]'::jsonb),
       (1002, now(), null, 'KR', 5000, 500, null),
       (1002, now() - interval '1 hour', null, 'KR', 6000, 600, null),
       (1002, now() - interval '15 day', null, 'KR', 7000, 800, null);
