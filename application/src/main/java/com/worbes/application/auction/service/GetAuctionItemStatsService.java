package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionItemStatsQuery;
import com.worbes.application.auction.port.in.GetAuctionItemStatsResult;
import com.worbes.application.auction.port.in.GetAuctionItemStatsUseCase;
import com.worbes.application.auction.port.out.FindActiveAuctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAuctionItemStatsService implements GetAuctionItemStatsUseCase {

    private final FindActiveAuctionPort findActiveAuctionPort;

    @Override
    public GetAuctionItemStatsResult execute(GetAuctionItemStatsQuery query) {
        List<Auction> auctions = findActiveAuctionPort.findActive(query);
        if (auctions.isEmpty()) {
            throw new IllegalStateException("No active auctions found");
        }
        TreeMap<Long, Integer> quantityByPrice = auctions.stream()
                .collect(Collectors.groupingBy(
                        Auction::getPrice,
                        TreeMap::new,
                        Collectors.summingInt(Auction::getQuantity)
                ));
        Long lowestPrice = quantityByPrice.firstKey();
        int totalQuantity = auctions.stream()
                .mapToInt(Auction::getQuantity)
                .sum();

        return new GetAuctionItemStatsResult(lowestPrice, totalQuantity, quantityByPrice);
    }
}
