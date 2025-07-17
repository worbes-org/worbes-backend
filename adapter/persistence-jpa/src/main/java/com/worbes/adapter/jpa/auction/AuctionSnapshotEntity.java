package com.worbes.adapter.jpa.auction;

import com.worbes.adapter.jpa.item.ItemEntity;
import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Entity
@Table(name = "auction_snapshot")
@Getter
@Setter
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_auction_snapshot_item_id")
    )
    private ItemEntity item;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    private RegionType region;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "lowest_price", nullable = false)
    private Long lowestPrice;

    @Column(name = "realm_id")
    private Long realmId;

    @Column(name = "bonus_list")
    private String bonusList;

    @Column(name = "bonus_level")
    private Integer bonusLevel;

    @Column(name = "base_level")
    private Integer baseLevel;

    private String suffix;
}
