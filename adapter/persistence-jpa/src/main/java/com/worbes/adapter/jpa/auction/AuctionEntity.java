package com.worbes.adapter.jpa.auction;

import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;

@Table(name = "auction")
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "ended_at")
    private Instant endedAt;

    @Builder
    private AuctionEntity(
            Long id,
            Long itemId,
            Integer quantity,
            Long price,
            RegionType region,
            Long realmId
    ) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.region = region;
        this.realmId = realmId;
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
