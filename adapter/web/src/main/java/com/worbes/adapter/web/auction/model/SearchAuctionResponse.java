package com.worbes.adapter.web.auction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchAuctionResponse(
        @JsonProperty("item_id") Long itemId,
        @JsonProperty("item_bonus") String itemBonus,
        @JsonProperty("item_level") Integer itemLevel,
        @JsonProperty("crafting_tier") Integer craftingTier,
        @JsonProperty("lowest_price") Long lowestPrice,
        @JsonProperty("total_quantity") Integer totalQuantity
) {
}
