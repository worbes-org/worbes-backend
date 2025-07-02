package com.worbes.web.auction.model;

import com.worbes.application.auction.model.Price;

public record PriceResponse(
        Long gold,
        Long silver,
        Long copper
) {
    public PriceResponse(Price price) {
        this(price.getGold(), price.getSilver(), price.getCopper());
    }
}
