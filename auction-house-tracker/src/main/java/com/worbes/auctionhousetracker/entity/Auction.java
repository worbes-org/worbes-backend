package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.TimeLeft;
import jakarta.persistence.Entity;
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

    private TimeLeft timeLeft;

    public Auction(Long id, Long itemId, Long quantity, Long unitPrice) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Auction(AuctionResponse.AuctionDto dto) {
        this.id = dto.getId();
        this.itemId = dto.getItemId();
        this.quantity = dto.getQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.timeLeft = TimeLeft.valueOf(dto.getTimeLeft());
    }
}
