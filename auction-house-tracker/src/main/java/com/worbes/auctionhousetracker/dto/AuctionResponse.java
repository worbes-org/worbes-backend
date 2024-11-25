package com.worbes.auctionhousetracker.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuctionResponse {
    private long id;

    @JsonProperty("item.id")
    private long itemId;

    private int quantity;

    @JsonProperty("unit_price")
    private long unitPrice;

    @JsonProperty("time_left")
    private String timeLeft;
}
