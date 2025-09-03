package com.worbes.adapter.persistence.jpa.auction;

import com.worbes.adapter.persistence.jpa.item.ItemEntity;
import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.realm.model.RegionType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "auction_snapshot_with_item_view")
@Getter
@Immutable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionSnapshotWithItemView {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private ItemEntity item;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "lowest_price", nullable = false)
    private Long lowestPrice;

    @Type(JsonType.class)
    @Column(name = "item_bonus", columnDefinition = "jsonb")
    private List<Long> itemBonus;

    @Column(name = "bonus_level")
    private Integer bonusLevel;

    @Column(name = "base_level")
    private Integer baseLevel;

    private String suffix;

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    private RegionType region;

    @Column(name = "realm_id")
    private Long realmId;

    @Column(nullable = false)
    private Instant time;

    public AuctionSnapshot toDomain() {
        return new AuctionSnapshot(
                this.time,
                this.item.toDomain(),
                this.region,
                this.realmId,
                this.totalQuantity,
                this.lowestPrice,
                this.itemBonus,
                this.bonusLevel,
                this.baseLevel,
                this.suffix
        );
    }
}
