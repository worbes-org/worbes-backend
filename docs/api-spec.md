# 📑 Worbes API Spec

## 개요

Worbes 프로젝트에서 제공하는 API 스펙입니다.

- 경매(Auction) 관련 API: 검색, 상세 조회
- 서버(Realm) 조회 API
- 모든 요청/응답은 JSON 형식

---

# 1. Auction Search API

## 개요

경매장에서 특정 조건으로 아이템을 검색한 뒤, 최저가·총 수량 등 요약 정보를 반환합니다.

## Endpoint

- **URL**: `/api/v1/auctions`
- **Method**: `GET`
- **Produces**: `application/json`

### Query Parameters

| 이름             | 타입      | 필수 여부 | 제약 조건                                  | 설명             |
|----------------|---------|-------|----------------------------------------|----------------|
| `region`       | String  | ✅     | `US`, `EU`, `KR` 등 `RegionType` Enum 값 | 지역             |
| `realmId`      | Long    | ✅     |                                        | 서버 ID          |
| `name`         | String  | ❌     | 최대 100자                                | 아이템 이름 (부분 검색) |
| `classId`      | Long    | ❌     | `subclassId`가 존재할 경우 **필수**            | 아이템 클래스 ID     |
| `subclassId`   | Long    | ❌     | `classId`가 null일 경우 사용할 수 없음           | 아이템 서브클래스 ID   |
| `minQuality`   | Integer | ❌     | `1` 이상                                 | 최소 아이템 등급      |
| `maxQuality`   | Integer | ❌     | `6` 이하                                 | 최대 아이템 등급      |
| `minItemLevel` | Integer | ❌     | `1` 이상                                 | 최소 아이템 레벨      |
| `maxItemLevel` | Integer | ❌     | `999` 이하                               | 최대 아이템 레벨      |
| `expansionId`  | Integer | ❌     | `1 ~ 11`                               | 확장팩 ID         |
| `page`         | Integer | ❌     | 기본값 `0`                                | 페이지 번호         |
| `size`         | Integer | ❌     | 기본값 `100`                              | 페이지 크기         |

### Response

```json
{
  "content": [
    {
      "item_id": 19019,
      "item_bonus": "100:200:300",
      "item_level": 80,
      "crafting_tier": 3,
      "lowest_price": 500000,
      "total_quantity": 12
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 100,
    "offset": 0,
    "paged": true,
    "unpaged": false},
  "size": 100,
  "number": 0,
  "sort": { "empty": true, "sorted": false, "unsorted": true },
  "first": true,
  "last": false,
  "numberOfElements": 1,
  "empty": false
  }
```

---

# 2. Auction Detail API

## 개요

특정 아이템 ID에 대한 경매 상세 정보를 조회합니다.

- 현재 최저가, 총 수량, 가격별 수량
- 최근 14일간 경매 가격 통계 및 추세

## Endpoint

- **URL**: `/api/v1/auctions/{itemId}`
- **Method**: `GET`
- **Produces**: `application/json`

### Path Parameter

| 이름       | 타입   | 필수 여부 | 설명         |
|----------|------|-------|------------|
| `itemId` | Long | ✅     | 조회할 아이템 ID |

### Query Parameters

| 이름          | 타입     | 필수 여부 | 설명                         |
|-------------|--------|-------|----------------------------|
| `region`    | String | ✅     | 지역 (예: KR, US, EU)         |
| `realmId`   | Long   | ✅     | 서버 ID                      |
| `itemBonus` | String | ❌     | 보너스 ID 목록 (`:` 구분), 없으면 전체 |

### Response

- **Body**: `ApiResponse<GetAuctionDetailResponse>`

```json
{
  "data": {
    "item_id": 19019,
    "item_bonus": "100:200",
    "lowest_price": 250000,
    "total_quantity": 5,
    "current_auctions": {
      "250000": 2,
      "300000": 3
    },
    "stats": {
      "average_lowest_price": 260000,
      "median_lowest_price": 250000,
      "trends": [
        {
          "time": "2025-08-26T10:00:00Z",
          "lowest_price": 240000,
          "total_quantity": 4
        },
        {
          "time": "2025-08-25T10:00:00Z",
          "lowest_price": 260000,
          "total_quantity": 3
        }
      ]
    }
  }
}
```

### Response Fields

| 필드                            | 타입                 | 설명                             |
|-------------------------------|--------------------|--------------------------------|
| `item_id`                     | Long               | 아이템 ID                         |
| `item_bonus`                  | String             | 보너스 ID 목록 (`:` 구분, 없으면 `null`) |
| `lowest_price`                | Long               | 현재 최저가                         |
| `total_quantity`              | Integer            | 현재 총 수량                        |
| `current_auctions`            | Map<Long, Integer> | 가격별 수량 (`가격 → 수량`)             |
| `stats.average_lowest_price`  | Long               | 최근 14일 평균 최저가                  |
| `stats.median_lowest_price`   | Long               | 최근 14일 최저가 중앙값                 |
| `stats.trends`                | List<AuctionTrend> | 일별 경매 가격 추세                    |
| `stats.trends.time`           | Instant            | 시점                             |
| `stats.trends.lowest_price`   | Long               | 해당 시점 최저가                      |
| `stats.trends.total_quantity` | Integer            | 해당 시점 총 수량                     |

---

# 3. Realm List API

## 개요

지정한 지역(`region`)에 속한 서버(Realm) 목록을 조회합니다.

- 각 서버의 이름, ID, 연결된 서버 ID 제공

## Endpoint

- **URL**: `/api/v1/realms`
- **Method**: `GET`
- **Produces**: `application/json`

### Query Parameters

| 이름       | 타입     | 필수 여부 | 설명                                      |
|----------|--------|-------|-----------------------------------------|
| `region` | String | ✅     | 조회할 서버 지역 (`RegionType`, 예: KR, US, EU) |

### Response

- **Body**: `ApiResponse<List<GetRealmResponse>>`

```json
{
  "content": [
    {
      "name": {
        "en_US": "Azshara",
        "ko_KR": "아즈샤라"
      },
      "id": 1234,
      "connected_realm_id": 5678
    },
    {
      "name": {
        "en_US": "Durotan",
        "ko_KR": "듀로탄"
      },
      "id": 2345,
      "connected_realm_id": 5678
    }
  ]
}
```

### Response Fields

| 필드                   | 타입                 | 설명                       |
|----------------------|--------------------|--------------------------|
| `name`               | Map<String,String> | 서버 이름, `locale -> 이름` 매핑 |
| `id`                 | Long               | 서버 ID                    |
| `connected_realm_id` | Long               | 연결된 서버 ID                |
