package com.worbes.adapter.web.auction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.application.auction.model.AuctionTrendPoint;

import java.time.Instant;
import java.util.List;

public record GetAuctionTrendResponse(
        @JsonProperty("average_lowest_price") Long averageLowestPrice,
        @JsonProperty("median_lowest_price") Long medianLowestPrice,
        @JsonProperty("trends") List<AuctionTrendDto> trends
) {
    public record AuctionTrendDto(
            @JsonProperty("time") Instant time,
            @JsonProperty("lowest_price") Long lowestPrice,
            @JsonProperty("total_quantity") Integer totalQuantity
    ) {
        public AuctionTrendDto(AuctionTrendPoint point) {
            this(point.time(), point.lowestPrice(), point.totalQuantity());
        }
    }
}
