package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CommodityListResponse(
        @NotNull @JsonProperty("auctions") List<CommodityResponse> auctions
) {
    public record CommodityResponse(
            @NotNull @JsonProperty("id") Long id,
            @NotNull @JsonProperty("quantity") @Min(1) Integer quantity,
            @NotNull @JsonProperty("unit_price") @Min(100) Long unitPrice,
            @NotNull @JsonProperty("item") AuctionItemResponse item
    ) {
    }
}
