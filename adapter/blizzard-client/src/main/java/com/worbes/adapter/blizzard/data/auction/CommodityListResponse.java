package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommodityListResponse {

    private List<CommodityResponse> auctions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommodityResponse {
        private Long id;
        private Long itemId;
        private Long quantity;

        @JsonProperty("unit_price")
        private Long unitPrice;

        @JsonProperty("item")
        private void unpackNestedItem(Map<String, Object> item) {
            if (item != null) {
                this.itemId = ((Number) item.get("id")).longValue();
            }
        }
    }
}
