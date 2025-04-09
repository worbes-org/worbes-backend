package com.worbes.auctionhousetracker.dto;

import lombok.Data;

@Data
public class AuctionDto {
    private Long auctionId;
    private Long itemId;
    private Long quantity;
    private Long unitPrice;
    private Long buyout;
}
