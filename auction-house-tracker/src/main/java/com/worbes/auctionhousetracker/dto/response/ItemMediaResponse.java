package com.worbes.auctionhousetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemMediaResponse {
    private ItemResponse itemResponse;
    private MediaResponse mediaResponse;
}
