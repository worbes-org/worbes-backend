package com.worbes.adapter.jpa.bonus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "auction_bonus",
        uniqueConstraints = @UniqueConstraint(columnNames = {"auction_id", "item_bonus_id"})
)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionBonusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "auction_id")
    private Long auctionId;

    @Column(nullable = false, name = "item_bonus_id")
    private Long itemBonusId;

    public AuctionBonusEntity(Long auctionId, Long itemBonusId) {
        this.auctionId = auctionId;
        this.itemBonusId = itemBonusId;
    }
}
