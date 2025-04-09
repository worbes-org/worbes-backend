package com.worbes.auctionhousetracker.dto.mapper;

import com.worbes.auctionhousetracker.dto.AuctionDto;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import lombok.Data;

import java.util.List;

@Data
public class AuctionUpdateCommand {
    private List<AuctionDto> auctions;
    private RegionType region;
    private Long realmId;
}
