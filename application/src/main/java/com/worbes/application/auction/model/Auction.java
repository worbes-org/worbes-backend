package com.worbes.application.auction.model;

import com.worbes.application.realm.model.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
public class Auction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final Long itemId;
    private final Long realmId;
    private final Long quantity;
    private final Long unitPrice;
    private final Long buyout;
    private final RegionType region;
    private final boolean active;

    @Builder
    private Auction(
            Long id,
            Long itemId,
            Long realmId,
            Long quantity,
            Long unitPrice,
            Long buyout,
            RegionType region,
            boolean active
    ) {
        this.id = id;
        this.itemId = itemId;
        this.realmId = realmId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.buyout = buyout;
        this.region = region;
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return Objects.equals(id, auction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
