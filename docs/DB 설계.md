<aside>
💡

월드 오브 워크래프트(WoW) 경매장 정보를 조회하고, 통계와 분석 결과를 제공하는 애플리케이션

</aside>

# 주요 기능

## **경매장 데이터 수집**

- 블리자드의 WoW API를 통해 Region(예: KR, US 등)과 Realm(예: KR-Azshara, US-Alleria 등)별로 **경매 정보를 주기적으로 불러오기**
- 불러온 데이터를 DB에 저장 (아이템 정보, 가격, 수량, 만료 시간 등)

## **경매 상품 조회**

- **Region + Realm**을 기준으로 현재 진행 중인 경매 상품 목록을 조회할 수 있음
- **아이템 클래스/서브클래스/인벤토리 타입** 필터: 예) 무기-양손 둔기, 방어구-판금-머리
- **아이템 레벨 범위**로 필터
- **희귀도**(rarity) 범위로 필터 (예: Poor~Legendary)

## **정렬/검색 기능**

- **가격 낮은 순**, 가격 높은 순, 이름 순, 아이템 레벨 순, 재고(총 수량) 순 등으로 정렬
- 아이템 이름(다국어)에 대한 검색(부분 일치 등)

## **다국어 지원**

- 아이템 이름, 아이템 클래스 이름, 서브클래스 이름, 인벤토리 타입 이름, Realm 이름 등을 **선택한 언어(locale)에 맞춰 표시**
- 예: ko_KR, en_US, fr_FR 등

## **통계 및 분석 기능**

- **일간/주간 최저가 변화**: “하루 전 대비 +100골드(+10%)”
- **재고(총 거래 수량) 변동**: “-1,500개 (현재 76,336개)”
- 특정 직업/전문기술 관련 아이템(약초, 광물 등)만 모아서 **동향 표**로 확인

## **히스토리/로그**

- 경매 종료 시점 가격, 거래량을 저장하여 **장기 통계** (예: 1주, 1개월)
- “가장 낮은 가격이 언제 얼마였는지” 등 히스토리 그래프

## **사용자 편의 기능**

- 즐겨찾는 아이템/검색 조건 저장
- 알림(특정 아이템 가격이 n 골드 이하가 되면 알림)

# 논리 설계

## 서버 (realm)

| 컬럼명                  | 타입      | 제약 조건                | 설명                                      |
|----------------------|---------|----------------------|-----------------------------------------|
| `id`                 | 정수      | 정수형 PK, 자동 증가        | 테이블 기본 키                                |
| `connected_realm_id` | 정수      | NOT NULL             | 연결된 Realm 식별 (WoW에서 Connected Realm 용도) |
| `region`             | 문자열(10) | NOT NULL             | ‘KR’, ‘US’ 등. (Enum)                    |
| `slug`               | 문자열(50) | UNIQUE(region, slug) | WoW API 식별자                             |

## 서버 다국어 (realm_translation)

| 컬럼명        | 타입      | 제약 조건                    | 설명                  |
|------------|---------|--------------------------|---------------------|
| `id`       | 정수      | PK                       | 테이블 기본 키            |
| `realm_id` | 정수      | FK, NOT NULL             | realm.id 참조         |
| `name`     | 문자열(50) | NOT NULL                 | 예: '아즈샤라', ‘하이잘'…   |
| `locale`   | 문자열(10) | NOT NULL                 | 예: 'ko_KR', 'en_US’ |
|            |         | UNIQUE(realm_id, locale) | 동일 서버+언어 중복 방지      |

## 아이템 클래스(item_class)

| 컬럼명  | 타입 | 제약 조건  | 설명       |
|------|----|--------|----------|
| `id` | 정수 | 정수형 PK | 테이블 기본 키 |

## 아이템 클래스 다국어(item_class_translation)

| 컬럼명             | 타입      | 제약 조건                         | 설명                  |
|-----------------|---------|-------------------------------|---------------------|
| `id`            | 정수      | PK                            | 테이블 기본 키            |
| `item_class_id` | 정수      | FK, NOT NULL                  | item_class.id 참조    |
| `name`          | 문자열(50) | NOT NULL                      | 예: '무기', ‘방어구'…     |
| `locale`        | 문자열(10) | NOT NULL                      | 예: 'ko_KR', 'en_US’ |
|                 |         | UNIQUE(item_class_id, locale) | 동일 아이템 클래스+언어 중복 방지 |

## 아이템 서브클래스(item_subclass)

| 컬럼명               | 타입 | 제약 조건                                    | 설명                                         |
|-------------------|----|------------------------------------------|--------------------------------------------|
| `id`              | 정수 | 정수형 PK, 자동 증가                            | 내부 PK (서로게이트 키)                            |
| `item_class_id`   | 정수 | FK, NOT NULL                             | item_class.id 참조                           |
| `subclass_api_id` | 정수 | NOT NULL                                 | Blizzard API의 item_subclass_id             |
|                   |    | UNIQUE (item_class_id, subclass_api_id), | 하나의 item_class 내에서 subclass_api_id가 유일해야 함 |

## 아이템 서브클래스 다국어(item_subclass_translation)

| 컬럼명                | 타입      | 제약 조건                                | 설명                                |
|--------------------|---------|--------------------------------------|-----------------------------------|
| `id`               | 정수      | PK                                   | 테이블 기본 키                          |
| `item_class_pk_id` | 정수      | FK, NOT NULL                         | item_subclass.id 참조               |
| `name`             | 문자열(50) | NOT NULL, 중복 가능                      | 예: ‘도끼’, ‘둔기’                     |
| `verbose`          | 문자열(50) | NULLABLE                             | 예: ‘한손 도끼류’, ‘양손 도끼류’             |
| `locale`           | 문자열(10) | NOT NULL                             | 예: 'ko_KR', 'en_US’               |
|                    |         | UNIQUE (item_subclass_pk_id, locale) | 하나의 서브클래스 + 특정 언어 조합이 중복되지 않도록 함. |

## 아이템(Item)

| 컬럼명                | 타입      | 제약 조건                             | 설명                         |
|--------------------|---------|-----------------------------------|----------------------------|
| `id`               | 정수      | PK, 자동 증가                         | 내부 PK (서로게이트 키)            |
| `blizzard_item_id` | 정수      | UNIQUE, NOT NULL                  | 블리자드 API의 아이템 ID           |
| `item_class_id`    | 정수      | FK → `item_class.id`, NOT NULL    | 공통 분류                      |
| `item_subclass_id` | 정수      | FK → `item_subclass.id`, NOT NULL | 세부 분류                      |
| `quality`          | 문자열(20) | NOT NULL                          | 'COMMON', 'EPIC' 등         |
| `level`            | 정수      | NOT NULL                          | 아이템 레벨                     |
| `required_level`   | 정수      | NOT NULL                          | 착용 최소 레벨                   |
| `inventory_type`   | 문자열(20) | NOT NULL                          | 'HEAD', 'CHEST', 'WAIST' 등 |

## 아이템 다국어(item_translation)

| 컬럼명            | 타입      | 제약 조건                    | 설명                                               |
|----------------|---------|--------------------------|--------------------------------------------------|
| `id`           | 정수      | PK                       | 테이블 기본 키                                         |
| `item_id`      | 정수      | FK, NOT NULL             | item.id 참조                                       |
| `name`         | 문자열(50) | NOT NULL                 | 예: ‘유혹방울’, ‘점등원의 절단기’                            |
| `preview_item` | JSONB   | NOT NULL                 | 아이템 요약 정보                                        |
| `locale`       | 문자열(10) | NOT NULL                 | 예: 'ko_KR', 'en_US’                              |
|                |         | UNIQUE (item_id, locale) | 동일 아이템, 동일 언어(locale)에 대해 **중복 번역**이 들어가지 않도록 제약 |

## 경매(Auction)

| 컬럼명          | 타입      | 제약 조건                                       | 설명                          |
|--------------|---------|---------------------------------------------|-----------------------------|
| `id`         | 정수      | PK                                          | 내부 PK (서로게이트 키)             |
| `auction_id` | 정수      | UNIQUE, NOT NULL                            | 블리자드 API의 옥션 ID             |
| `region`     | 문자열(10) | NOT NULL                                    | ‘KR’, ‘US’ 등                |
| `realm_id`   | 정수      | FK → `realm.id`, NULL 가능                    | Commodities면 NULL로 두거나 별도 값 |
| `item_id`    | 정수      | FK → `item.id`                              | 아이템 아이디                     |
| `quantity`   | 정수      | NOT NULL                                    | 재고 개수                       |
| `unit_price` | 정수      | NULL 가능                                     | Commodities면 `unit_price`   |
| `buyout`     | 정수      | NULL 가능                                     | 일반 경매면 `buyout`             |
| `active`     | bool    | NOT NULL, 기본값 true                          | 경매 활성화 여부                   |
|              |         | `unit_price`, `buyout` 둘 중 하나는 NULL이 아니여야 함 |                             |
