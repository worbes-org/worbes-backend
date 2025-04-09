package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.AuctionDto;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Auction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    @SequenceGenerator(name = "auction_seq", sequenceName = "auction_seq", allocationSize = 500)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long auctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private Long quantity;

    private Long unitPrice;

    private Long buyout;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id")
    private Realm realm;

    @Column(nullable = false)
    private boolean active;

    public static AuctionBuilder builder() {
        return new AuctionBuilder();
    }

    public static Auction from(AuctionDto dto, Item item, Realm realm, RegionType region) {
        return Auction.builder()
                .auctionId(dto.getAuctionId())
                .item(item)
                .quantity(dto.getQuantity())
                .unitPrice(dto.getUnitPrice())
                .buyout(dto.getBuyout())
                .region(region)
                .realm(realm)
                .build();
    }

    public void end() {
        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return Objects.equals(auctionId, auction.auctionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(auctionId);
    }

    public static class AuctionBuilder {
        private Long auctionId;
        private Item item;
        private Long quantity;
        private Long unitPrice;
        private Long buyout;
        private RegionType region;
        private Realm realm;

        public AuctionBuilder auctionId(Long auctionId) {
            this.auctionId = auctionId;
            return this;
        }

        public AuctionBuilder item(Item item) {
            this.item = item;
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

        public AuctionBuilder region(RegionType region) {
            this.region = region;
            return this;
        }

        public AuctionBuilder realm(Realm realm) {
            this.realm = realm;
            return this;
        }

        public Auction build() {
            Auction auction = new Auction();
            auction.setAuctionId(this.auctionId);
            auction.setItem(this.item);
            auction.setQuantity(this.quantity);
            auction.setUnitPrice(this.unitPrice);
            auction.setBuyout(this.buyout);
            auction.setRegion(this.region);
            auction.setRealm(this.realm);
            auction.setActive(true);
            return auction;
        }
    }
}
