package com.worbes.auctionhousetracker.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Long quantity;

    private Long unitPrice;

    private Long buyout;

    @Enumerated(EnumType.STRING)
    private Region region;

    private Long realmId;

    private boolean active = true;

    public static AuctionBuilder builder() {
        return new AuctionBuilder();
    }

    public void end() {
        this.active = false;
    }

    public static class AuctionBuilder {
        private Long auctionId;
        private Item item;
        private Long quantity;
        private Long unitPrice;
        private Long buyout;
        private Region region;
        private Long realmId;

        public AuctionBuilder auctionId(Long auctionId) {
            this.auctionId = auctionId;
            return this;
        }

        public AuctionBuilder quantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public AuctionBuilder unitPrice(Long unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public AuctionBuilder buyout(Long buyout) {
            this.buyout = buyout;
            return this;
        }

        public AuctionBuilder region(Region region) {
            this.region = region;
            return this;
        }

        public AuctionBuilder realmId(Long realmId) {
            this.realmId = realmId;
            return this;
        }

        public AuctionBuilder item(Item item) {
            this.item = item;
            return this;
        }

        public Auction build() {
            Auction auction = new Auction();
            auction.auctionId = this.auctionId;
            auction.quantity = this.quantity;
            auction.unitPrice = this.unitPrice;
            auction.buyout = this.buyout;
            auction.region = this.region;
            auction.realmId = this.realmId;
            auction.item = this.item;
            return auction;
        }
    }
}
