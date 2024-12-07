package com.worbes.auctionhousetracker.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AuctionResponse {
    private long id;

    private Map<String, Long> item;

    private long quantity;

    @JsonProperty("unit_price")
    private long unitPrice;

    @JsonProperty("time_left")
    private String timeLeft;
}
