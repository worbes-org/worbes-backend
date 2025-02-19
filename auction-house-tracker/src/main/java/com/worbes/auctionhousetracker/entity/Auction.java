package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.entity.enums.TimeLeft;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@AllArgsConstructor
public class Auction {

    @Id
    private Long id;

    private Long itemId;

    private Long quantity;

    private Long unitPrice;

    @Enumerated(EnumType.STRING)
    private TimeLeft timeLeft;

    @Enumerated(EnumType.STRING)
    private Region region;

    public Auction(AuctionResponse.AuctionDto dto, Region region) {
        this.id = dto.getId();
        this.itemId = dto.getItemId();
        this.quantity = dto.getQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.timeLeft = TimeLeft.valueOf(dto.getTimeLeft());
        this.region = region;
    }
}
