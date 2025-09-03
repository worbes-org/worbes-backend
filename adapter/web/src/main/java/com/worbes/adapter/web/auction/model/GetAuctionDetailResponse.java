package com.worbes.adapter.web.auction.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record GetAuctionDetailResponse(
        @JsonProperty("item_id") Long itemId,
        @JsonProperty("item_bonus") String itemBonus,
        @JsonProperty("lowest_price") long lowestPrice,
        @JsonProperty("total_quantity") int totalQuantity,
        @JsonProperty("current_auctions") Map<Long, Integer> quantityByPrice,
        @JsonProperty("stats") GetAuctionTrendResponse historyResponse
) {
}
