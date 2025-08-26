package com.worbes.application.auction.model;

import com.worbes.application.realm.model.RegionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class Auction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final Long itemId;
    private final Long realmId;
    private final Long price;
    private final RegionType region;
    private final List<Long> itemBonus;
    private final Integer quantity;

    private Auction(Builder builder) {
        this.id = builder.id;
        this.itemId = builder.itemId;
        this.realmId = builder.realmId;
        this.price = builder.price;
        this.region = builder.region;
        this.itemBonus = builder.itemBonus;
        this.quantity = builder.quantity;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private Long id;
        private Long itemId;
        private Long realmId;
        private Long price;
        private RegionType region;
        private List<Long> itemBonus;
        private Integer quantity;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder itemId(Long itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder realmId(Long realmId) {
            this.realmId = realmId;
            return this;
        }

        public Builder unitPrice(Long unitPrice) {
            this.price = unitPrice;
            return this;
        }

        public Builder buyout(Long buyout) {
            this.price = buyout;
            return this;
        }

        public Builder bid(Long bid) {
            if (this.price == null) {
                this.price = bid;
            }
            return this;
        }

        public Builder region(RegionType region) {
            this.region = region;
            return this;
        }

        public Builder itemBonus(List<Long> itemBonus) {
            this.itemBonus = itemBonus;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Auction build() {
            Objects.requireNonNull(id, "Auction ID cannot be null");
            Objects.requireNonNull(itemId, "Item ID cannot be null");
            Objects.requireNonNull(price, "Price cannot be null");
            Objects.requireNonNull(region, "Region cannot be null");
            Objects.requireNonNull(quantity, "Quantity cannot be null");

            return new Auction(this);
        }
    }
}
