package com.worbes.adapter.persistence.jpa.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Table(name = "auction")
@Getter
@Setter
@Entity
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuctionEntity {

    @Id
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false)
    @Min(1L)
    private Integer quantity;

    @Column(nullable = false)
    @Min(100L)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @Column(name = "realm_id")
    private Long realmId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Type(JsonType.class)
    @Column(name = "item_bonus", columnDefinition = "jsonb")
    private List<Long> itemBonus;

    public AuctionEntity(
            Long id,
            Long itemId,
            Integer quantity,
            Long price,
            RegionType region,
            Long realmId,
            List<Long> itemBonus
    ) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.region = region;
        this.realmId = realmId;
        this.itemBonus = itemBonus;
    }

    public static AuctionEntity from(Auction auction) {
        return new AuctionEntity(
                auction.getId(),
                auction.getItemId(),
                auction.getQuantity(),
                auction.getPrice(),
                auction.getRegion(),
                auction.getRealmId(),
                auction.getItemBonus()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuctionEntity that = (AuctionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
