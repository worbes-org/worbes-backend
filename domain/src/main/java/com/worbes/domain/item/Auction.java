package com.worbes.domain.item;

import com.worbes.domain.shared.RegionType;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder
public class Auction {

    private final Long id;
    private final Item item;
    private final Realm realm;
    private final RegionType region;
    private final Long quantity;
    private final Long unitPrice;
    private final Long buyout;
    @Getter
    private boolean active;

    public static Auction create(Long id,
                                 Item item,
                                 Realm realm,
                                 RegionType region,
                                 Long quantity,
                                 Long unitPrice,
                                 Long buyout) {
        return Auction.builder()
                .id(id)
                .item(item)
                .realm(realm)
                .region(region)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .buyout(buyout)
                .active(true)
                .build();
    }

    public void end() {
        this.active = false;
    }

    public boolean isCommodity() {
        return unitPrice != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Auction auction = (Auction) o;
        return Objects.equals(id, auction.id) && region == auction.region;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, region);
    }
}
