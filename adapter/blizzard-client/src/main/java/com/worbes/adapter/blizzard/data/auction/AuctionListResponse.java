package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionListResponse {

    private List<AuctionResponse> auctions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuctionResponse {
        private long id;
        private long itemId;
        private long quantity;
        private long buyout;

        @JsonProperty("unit_price")
        private long unitPrice;

        @JsonProperty("item")
        private void unpackNestedItem(Map<String, Object> item) {
            if (item != null) {
                this.itemId = ((Number) item.get("id")).longValue();
            }
        }
    }
}
