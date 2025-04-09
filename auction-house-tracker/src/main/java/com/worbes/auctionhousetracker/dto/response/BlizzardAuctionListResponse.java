package com.worbes.auctionhousetracker.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlizzardAuctionListResponse {
    private List<BlizzardAuctionResponse> auctions;
}
