package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AuctionHistory {
    private final Item item;
    private final List<AuctionHourlySummary> summaries;
}
