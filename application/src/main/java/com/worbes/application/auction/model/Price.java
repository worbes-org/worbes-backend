package com.worbes.application.auction.model;

import lombok.Getter;

import java.util.Objects;


@Getter
public class Price implements Comparable<Price> {

    private final Long totalCopper;

    public Price(Long totalCopper) {
        this.totalCopper = totalCopper;
    }

    public Long getGold() {
        return totalCopper / 10_000;
    }

    public Long getSilver() {
        return (totalCopper % 10_000) / 100;
    }

    public Long getCopper() {
        return totalCopper % 100;
    }

    @Override
    public String toString() {
        return String.format("%dg %ds %dc", getGold(), getSilver(), getCopper());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(totalCopper, price.totalCopper);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(totalCopper);
    }

    @Override
    public int compareTo(Price other) {
        return this.totalCopper.compareTo(other.totalCopper);
    }
}

