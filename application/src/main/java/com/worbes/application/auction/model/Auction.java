package com.worbes.application.auction.model;

import com.worbes.application.realm.model.RegionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Auction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final Long itemId;
    private final Long realmId;
    private final Long price;
    private final RegionType region;
    private final List<Long> itemBonus;
    @Setter
    private Integer quantity;
    @Setter
    private Instant endedAt;

    @Builder
    public Auction(
            Long id,
            Long itemId,
            Long realmId,
            Integer quantity,
            Long price,
            RegionType region,
            List<Long> itemBonus
    ) {
        this.id = id;
        this.itemId = itemId;
        this.realmId = realmId;
        this.quantity = quantity;
        this.price = price;
        this.region = region;
        this.endedAt = null;
        this.itemBonus = Optional.ofNullable(itemBonus).orElse(Collections.emptyList());
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

    public String itemBonusToString() {
        if (itemBonus == null || itemBonus.isEmpty()) {
            return null;
        }
        return itemBonus.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(":"));
    }
}
