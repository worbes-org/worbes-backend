package com.worbes.adapter.jpa.auction;

import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private Long quantity;

    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @Column(name = "realm_id")
    private Long realmId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Builder
    private AuctionEntity(
            Long auctionId,
            Long itemId,
            Long quantity,
            Long price,
            RegionType region,
            Long realmId,
            LocalDateTime endedAt
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
