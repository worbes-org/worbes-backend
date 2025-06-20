package com.worbes.web;

import com.worbes.application.auction.model.Price;

public record PriceResponse(
        int gold,
        int silver,
        int copper
) {
    public PriceResponse(Price price) {
        this(price.getGold(), price.getSilver(), price.getCopper());
    }
}
