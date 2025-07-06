package com.worbes.adapter.jpa.auction;

import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Table(name = "auction")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auction_id", nullable = false, unique = true)
    private Long auctionId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(nullable = false)
    @Min(0L)
    private Integer quantity;

    @Column(nullable = false)
    @Min(1L)
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
            Long auctionId,
            Long itemId,
            Integer quantity,
            Long price,
            RegionType region,
            Long realmId,
            Instant endedAt
    ) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.region = region;
        this.realmId = realmId;
        this.endedAt = endedAt;
    }
}
