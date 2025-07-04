DELETE FROM auction;
INSERT INTO auction (auction_id, item_id, quantity, price, region, realm_id, created_at, ended_at)
VALUES (1, 1, 10, 1000, 'KR', 101, NOW() - INTERVAL '30 days', NULL); 