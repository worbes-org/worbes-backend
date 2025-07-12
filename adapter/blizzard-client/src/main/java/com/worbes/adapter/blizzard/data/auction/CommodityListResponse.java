package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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

        @NotNull
        private Long id;

        @NotNull
        private Long itemId;

        @NotNull
        @Min(1)
        private Integer quantity;

        @NotNull
        @Min(100)
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
