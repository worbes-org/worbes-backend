INSERT INTO item (id,
                  name,
                  level,
                  class_id,
                  subclass_id,
                  inventory_type,
                  quality,
                  icon,
                  crafting_tier,
                  is_stackable,
                  expansion_id)
VALUES
-- id=1, "검", classId=1, subclassId=1, quality=2, expansionId=1
(1, '{
  "ko_KR": "검"
}'::jsonb, 10, 1, 1, 'WEAPON', 2, 'icon1', null, true, 1),
-- id=2, "방패", classId=1, subclassId=2, quality=3, expansionId=1
(2, '{
  "ko_KR": "방패"
}'::jsonb, 20, 1, 2, 'SHIELD', 3, 'icon2', null, false, 1),
-- id=3, "활", classId=2, subclassId=3, quality=4, expansionId=2
(3, '{
  "ko_KR": "활"
}'::jsonb, 30, 2, 3, 'RANGED', 4, 'icon3', null, true, 2);
