package com.worbes.auctionhousetracker.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AuctionResponse {
    private List<AuctionDto> auctions;

    @Data
    public static class AuctionDto {
        private long id;

        private long itemId;

        private long quantity;

        @JsonProperty("unit_price")
        private long unitPrice;

        @JsonProperty("time_left")
        private String timeLeft;

        @JsonProperty("item")
        private void mapItemId(Map<String, Long> item) {
            id = item.get("id");
        }
    }
}
