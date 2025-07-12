package com.worbes.adapter.jpa.auction;

import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Table(name = "auction")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(name = "auction_id", nullable = false, unique = true)
    private Long auctionId;

    @Getter
    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Getter
    @Setter
    @Column(nullable = false)
    @Min(1L)
    private Integer quantity;

    @Getter
    @Column(nullable = false)
    @Min(100L)
    private Long price;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RegionType region;

    @Getter
    @Column(name = "realm_id")
    private Long realmId;

    @Getter
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Getter
    @Setter
    @Column(name = "ended_at")
    private Instant endedAt;

    @Getter
    @Column(name = "item_bonus")
    private String itemBonus;

    @Builder
    private AuctionEntity(
            Long auctionId,
            Long itemId,
            Integer quantity,
            Long price,
            RegionType region,
            Long realmId,
            String itemBonus
    ) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.region = region;
        this.realmId = realmId;
        this.itemBonus = itemBonus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuctionEntity that = (AuctionEntity) o;
        return Objects.equals(auctionId, that.auctionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(auctionId);
    }

    public List<Long> itemBonusToList() {
        if (itemBonus == null || itemBonus.isBlank()) return Collections.emptyList();
        return Arrays.stream(itemBonus.split(":"))
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
