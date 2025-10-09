package com.worbes.adapter.blizzard.data.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuctionListResponse(
        @NotNull @JsonProperty("auctions") List<AuctionResponse> auctions
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AuctionResponse(
            @NotNull @JsonProperty("id") Long id,
            @NotNull @JsonProperty("quantity") @Min(1) Integer quantity,
            @JsonProperty("buyout") @Min(100) Long buyout,
            @JsonProperty("bid") @Min(100) Long bid,
            @NotNull @JsonProperty("item") AuctionItemResponse item
    ) {
    }
}
