package com.worbes.adapter.jpa.auction;

import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "auction_hourly_trend_view")
@Immutable
public class AuctionTrendView {

    @Id
    private LocalDateTime time;

    @Column(name = "item_id")
    private Long itemId;

    @Enumerated(EnumType.STRING)
    private RegionType region;

    @Column(name = "realm_id")
    private Long realmId;

    @Column(name = "total_quantity")
    private Long totalQuantity;

    @Column(name = "min_price")
    private Long minPrice;
}
