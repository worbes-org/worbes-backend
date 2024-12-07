package com.worbes.auctionhousetracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
    private Long id;

    @ManyToOne
    private Item item;

    private Long quantity;

    private Long unitPrice;

    public Auction(Long id, Item item, Long quantity, Long unitPrice) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
