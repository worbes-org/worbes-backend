DELETE FROM auction;
INSERT INTO auction (auction_id, item_id, quantity, price, region, realm_id, created_at, ended_at)
VALUES (1, 1, 10, 1000, 'KR', 101, NOW() - INTERVAL '4 hours', NULL),
       (2, 1, 5, 900, 'KR', 101, NOW() - INTERVAL '3 hours', NULL),
       (3, 1, 8, 950, 'KR', 101, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hours'),
       (4, 1, 3, 850, 'KR', 101, NOW() - INTERVAL '1 hours', NULL); 