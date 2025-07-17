package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CommodityResponse(
        @NotNull @JsonProperty("id") Long id,
        @NotNull @JsonProperty("quantity") Integer quantity,
        @NotNull @JsonProperty("unit_price") Long unitPrice,
        @NotNull @JsonProperty("item") AuctionItemResponse item
) {
}
