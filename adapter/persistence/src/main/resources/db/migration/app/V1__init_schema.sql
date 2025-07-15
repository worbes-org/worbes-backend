-- ================================
-- V1__init_schema.sql
-- ================================

-- auction 테이블
create table if not exists public.auction
(
    id         bigint      not null
        primary key,
    item_id    bigint      not null,
    price      bigint      not null CHECK (price >= 100),
    quantity   integer     not null CHECK (quantity >= 1),
    item_bonus varchar(255),
    region     varchar(10) not null
        constraint auction_region_check
            check ((region)::text = ANY ((ARRAY ['US'::character varying, 'KR'::character varying])::text[])),
    realm_id   bigint,
    created_at timestamptz(0),
    ended_at   timestamptz(0)
);
alter table public.auction
    owner to "worbes-admin";

-- item 테이블
create table if not exists public.item
(
    id             bigint       not null
        primary key,
    name           jsonb        not null,
    level          integer      not null,
    class_id       bigint       not null,
    subclass_id    bigint       not null,
    inventory_type varchar(20)  not null
        constraint item_inventory_type_check
            check ((inventory_type)::text = ANY
                   ((ARRAY ['NON_EQUIP'::character varying, 'HEAD'::character varying, 'NECK'::character varying, 'SHOULDER'::character varying, 'BODY'::character varying, 'CHEST'::character varying, 'WAIST'::character varying, 'LEGS'::character varying, 'FEET'::character varying, 'WRIST'::character varying, 'HAND'::character varying, 'FINGER'::character varying, 'TRINKET'::character varying, 'WEAPON'::character varying, 'SHIELD'::character varying, 'RANGED'::character varying, 'CLOAK'::character varying, 'TWOHWEAPON'::character varying, 'BAG'::character varying, 'TABARD'::character varying, 'ROBE'::character varying, 'WEAPONMAINHAND'::character varying, 'WEAPONOFFHAND'::character varying, 'HOLDABLE'::character varying, 'AMMO'::character varying, 'THROWN'::character varying, 'RANGEDRIGHT'::character varying, 'QUIVER'::character varying, 'RELIC'::character varying, 'PROFESSION_TOOL'::character varying, 'PROFESSION_GEAR'::character varying, 'EQUIPABLESPELL_OFFENSIVE'::character varying, 'EQUIPABLESPELL_UTILITY'::character varying, 'EQUIPABLESPELL_DEFENSIVE'::character varying, 'EQUIPABLESPELL_WEAPON'::character varying])::text[])),
    quality        varchar(20)  not null
        constraint item_quality_check
            check ((quality)::text = ANY
                   ((ARRAY ['POOR'::character varying, 'COMMON'::character varying, 'UNCOMMON'::character varying, 'RARE'::character varying, 'EPIC'::character varying, 'LEGENDARY'::character varying, 'ARTIFACT'::character varying, 'HEIRLOOM'::character varying, 'WOW_TOKEN'::character varying])::text[])),
    icon           varchar(255) not null,
    crafting_tier  smallint
        constraint item_crafting_tier_check
            check ((crafting_tier >= 0) AND (crafting_tier <= 4)),
    is_stackable   boolean      not null,
    expansion_id   smallint,
    created_at     timestamp(0),
    updated_at     timestamp(0)
);

alter table public.item
    owner to "worbes-admin";

-- realm 테이블
create table if not exists public.realm
(
    id                 bigint      not null
        primary key,
    region             varchar(10) not null
        constraint realm_region_check
            check ((region)::text = ANY ((ARRAY ['US'::character varying, 'KR'::character varying])::text[])),
    connected_realm_id bigint      not null,
    name               jsonb       not null,
    slug               varchar(50) not null,
    created_at         timestamp(0),
    updated_at         timestamp(0),
    constraint uq_region_slug
        unique (region, slug)
);

alter table public.realm
    owner to "worbes-admin";

---
CREATE TABLE if not exists item_bonus
(
    id         BIGINT PRIMARY KEY,
    suffix     VARCHAR(100),
    level      INT,
    base_level INT,
    created_at timestamp(0),
    updated_at timestamp(0)
);
alter table public.item_bonus
    owner to "worbes-admin";
---
CREATE TABLE if not exists auction_bonus
(
    id            BIGSERIAL PRIMARY KEY,
    auction_id    BIGINT NOT NULL,
    item_bonus_id BIGINT NOT NULL,
    UNIQUE (auction_id, item_bonus_id)
);
---
CREATE OR REPLACE VIEW auction_hourly_trend as
WITH hours as (SELECT DISTINCT DATE_TRUNC('hour', created_at) AS hour,
                               item_id,
                               item_bonus,
                               region,
                               realm_id
               FROM auction),
     summary AS (SELECT h.hour                                                                  AS time,
                        h.item_id,
                        h.region,
                        h.realm_id,
                        h.item_bonus,
                        (SELECT SUM(a.quantity)
                         FROM auction a
                         WHERE a.item_id = h.item_id
                           AND a.region = h.region
                           AND a.realm_id IS NOT DISTINCT FROM h.realm_id
                           and a.item_bonus IS NOT DISTINCT FROM h.item_bonus
                           AND DATE_TRUNC('hour', a.created_at) <= h.hour
                           AND (a.ended_at IS NULL OR DATE_TRUNC('hour', a.ended_at) > h.hour)) AS total_quantity,
                        (SELECT MIN(a.price)
                         FROM auction a
                         WHERE a.item_id = h.item_id
                           AND a.region = h.region
                           AND a.realm_id IS NOT DISTINCT FROM h.realm_id
                           and a.item_bonus IS NOT DISTINCT FROM h.item_bonus
                           AND DATE_TRUNC('hour', a.created_at) <= h.hour
                           AND (a.ended_at IS NULL OR DATE_TRUNC('hour', a.ended_at) > h.hour)) AS lowest_price
                 FROM hours h)
SELECT *
FROM summary
ORDER BY time;
