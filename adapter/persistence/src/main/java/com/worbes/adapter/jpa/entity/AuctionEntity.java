package com.worbes.adapter.jpa.entity;

import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "auction")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AuctionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_id", nullable = false, unique = true)
    private Long auctionId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false)
    private Long quantity;

    @Column(name = "unit_price")
    private Long unitPrice;

    private Long buyout;

    @Column(nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @Column(name = "realm_id")
    private Long realmId;

    @Builder
    private AuctionEntity(
            Long auctionId,
            Long itemId,
            Long quantity,
            Long unitPrice,
            Long buyout,
            boolean active,
            RegionType region,
            Long realmId
    ) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.buyout = buyout;
        this.active = active;
        this.region = region;
        this.realmId = realmId;
    }
}
