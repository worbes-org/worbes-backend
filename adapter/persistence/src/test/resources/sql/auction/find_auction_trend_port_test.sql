-- ============================
-- realm 데이터
-- ============================
INSERT INTO realm (id, region, connected_realm_id, name, slug, created_at, updated_at)
VALUES (205, 'KR', 100, '{
  "ko_KR": "테스트 렐름1"
}', 'testrealm1', now(), now()),
       (9999, 'KR', 100, '{
         "ko_KR": "테스트 렐름2"
       }', 'testrealm2', now(), now()),
       (5678, 'US', 1234, '{
         "en_US": "테스트 렐름3"
       }', 'testrealm_us', now(), now());

INSERT INTO auction_snapshot (item_id, time, realm_id, region, lowest_price, total_quantity, item_bonus)
VALUES (1234, now(), 205, 'KR', 10000, 1, '[
  100,
  101
]'::jsonb),
       (1234, now(), 205, 'KR', 12000, 1, '[
         101,
         100
       ]'::jsonb),
       (1234, now() - interval '1 day', 205, 'KR', 11000, 1, '[
         100,
         101
       ]'::jsonb),
       (1234, now() - interval '5 day', 205, 'KR', 14000, 1, '[
         100,
         101
       ]'::jsonb),
       ---아이템 아이디가 다른 경우
       (1111, now(), 205, 'KR', 18000, 1, '[
         100,
         101
       ]'::jsonb),
       ---realm이 다른 경우
       (1234, now(), 9999, 'KR', 19000, 1, '[
         100,
         101
       ]'::jsonb),
       ---아이템 보너스가 다른 경우
       (1234, now(), 205, 'KR', 15000, 1, '[
         100
       ]'::jsonb),
       (1234, now(), 205, 'KR', 13000, 1, null),
       (1234, now(), 205, 'KR', 16000, 1, '[
         999,
         100
       ]'::jsonb),
--US
       (1234, now(), 1234, 'US', 17000, 1, '[
         100,
         101
       ]'::jsonb);
