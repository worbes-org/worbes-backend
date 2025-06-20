package com.worbes.application.auction.model;

import lombok.Getter;

@Getter
public class Price {

    private final int totalCopper;

    public Price(Long totalCopper) {
        this.totalCopper = Math.toIntExact(totalCopper);
    }

    public int getGold() {
        return totalCopper / 10_000;
    }

    public int getSilver() {
        return (totalCopper % 10_000) / 100;
    }

    public int getCopper() {
        return totalCopper % 100;
    }

    @Override
    public String toString() {
        return String.format("%dg %ds %dc", getGold(), getSilver(), getCopper());
    }
}

