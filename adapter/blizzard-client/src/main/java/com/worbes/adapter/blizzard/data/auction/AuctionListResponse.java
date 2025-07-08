package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionListResponse {

    private List<AuctionResponse> auctions;

    @Data
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuctionResponse {

        @NotNull
        private Long id;

        @NotNull
        private Long itemId;

        @NotNull
        private Integer quantity;

        private Long buyout;

        private Long bid;

        @JsonProperty("item")
        private void unpackNestedItem(Map<String, Object> item) {
            if (item != null) {
                this.itemId = ((Number) item.get("id")).longValue();
            }
        }
    }
}
