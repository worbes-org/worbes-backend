package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionDetailQuery;
import com.worbes.application.auction.port.in.GetAuctionDetailResult;
import com.worbes.application.auction.port.in.GetAuctionDetailUseCase;
import com.worbes.application.auction.port.out.FindAuctionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAuctionDetailService implements GetAuctionDetailUseCase {

    private final FindAuctionPort findAuctionPort;

    @Override
    public GetAuctionDetailResult execute(GetAuctionDetailQuery query) {
        List<Auction> auctions = findAuctionPort.findBy(query);
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

        return new GetAuctionDetailResult(lowestPrice, totalQuantity, quantityByPrice);
    }
}
