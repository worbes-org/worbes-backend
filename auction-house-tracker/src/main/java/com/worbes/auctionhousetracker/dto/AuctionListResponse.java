package com.worbes.auctionhousetracker.dto;


import lombok.Data;

import java.util.List;

@Data
public class AuctionListResponse {
    private List<AuctionResponse> auctionResponses;
}
