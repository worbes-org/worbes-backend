package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.enums.Region;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auction_seq")
    @SequenceGenerator(name = "auction_seq", sequenceName = "auction_seq", allocationSize = 1000)
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
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "realm_id")
    private Realm realm;

    @Column(nullable = false)
    private boolean active = true;

    public void end() {
        this.active = false;
    }
}
