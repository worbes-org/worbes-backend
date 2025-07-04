package com.worbes.application.auction.model;

import com.worbes.application.item.model.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class AuctionDetail {
    private final Item item;
    private final List<Auction> auctions;
    private final List<AuctionStatsSnapshot> statsSnapshots;

    public Map<Long, Long> getAvailable() {
        return auctions.stream()
                .collect(Collectors.groupingBy(
                        Auction::getPrice,
                        TreeMap::new,
                        Collectors.summingLong(Auction::getQuantity)
                ));
    }
}
