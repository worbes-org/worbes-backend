package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuctionResponse(
        @NotNull @JsonProperty("id") Long id,
        @NotNull @JsonProperty("quantity") Integer quantity,
        @JsonProperty("buyout") Long buyout,
        @JsonProperty("bid") Long bid,
        @NotNull @JsonProperty("item") AuctionItemResponse item
) {
}
