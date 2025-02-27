package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    @SequenceGenerator(name = "auction_seq", sequenceName = "auction_seq", allocationSize = 1000)
    private Long id;

    @Column(unique = true)
    private Long auctionId;

    private Long itemId;

    private Long quantity;

    private Long unitPrice;

    @Enumerated(EnumType.STRING)
    private Region region;

    private Long realmId;

    private boolean active = true;

    public Auction(AuctionResponse.AuctionDto dto, Region region) {
        this.auctionId = dto.getId();
        this.itemId = dto.getItemId();
        this.quantity = dto.getQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.region = region;
        this.realmId = null;
    }

    public Auction(AuctionResponse.AuctionDto dto, Region region, Long realmId) {
        this.auctionId = dto.getId();
        this.itemId = dto.getItemId();
        this.quantity = dto.getQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.region = region;
        this.realmId = realmId;
    }

    public void end() {
        this.active = false;
    }
}
