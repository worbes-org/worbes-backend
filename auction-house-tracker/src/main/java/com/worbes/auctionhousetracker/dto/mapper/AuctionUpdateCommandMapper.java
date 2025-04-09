package com.worbes.auctionhousetracker.dto.mapper;

import com.worbes.auctionhousetracker.dto.AuctionDto;
import com.worbes.auctionhousetracker.dto.response.BlizzardAuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuctionUpdateCommandMapper {

    @Mapping(source = "id", target = "auctionId")
    @Mapping(source = "itemId", target = "itemId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(source = "buyout", target = "buyout")
    AuctionDto toAuctionDto(BlizzardAuctionResponse response);

    List<AuctionDto> toAuctionDtoList(List<BlizzardAuctionResponse> responses);

    default AuctionUpdateCommand toCommand(List<BlizzardAuctionResponse> responses, RegionType region, Long realmId) {
        AuctionUpdateCommand command = new AuctionUpdateCommand();
        command.setAuctions(toAuctionDtoList(responses));
        command.setRegion(region);
        command.setRealmId(realmId);
        return command;
    }
}
