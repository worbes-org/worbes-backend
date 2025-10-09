-- 최근 30일 이내 데이터 50개
INSERT INTO auction_snapshot (item_id, time, realm_id, region, lowest_price, total_quantity, item_bonus)
SELECT gs                                   AS item_id,
       NOW() - INTERVAL '1 day' * (gs % 30) AS time, -- 0~29일 전 순차
       1                                    AS realm_id,
       'KR'                                 AS region,
       (random() * 100000)::bigint + 1000   AS lowest_price,
       (random() * 500)::int + 1            AS total_quantity,
       '{}'::jsonb
FROM generate_series(1, 50) gs;

-- 30일 이상 지난 데이터 50개
INSERT INTO auction_snapshot (item_id, time, realm_id, region, lowest_price, total_quantity, item_bonus)
SELECT gs                                          AS item_id,
       NOW() - INTERVAL '1 day' * (30 + (gs % 30)) AS time, -- 30~59일 전 순차
       1                                           AS realm_id,
       'KR'                                        AS region,
       (random() * 100000)::bigint + 1000          AS lowest_price,
       (random() * 500)::int + 1                   AS total_quantity,
       '{}'::jsonb
FROM generate_series(51, 100) gs;
