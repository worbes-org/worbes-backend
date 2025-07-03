package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.SearchItemRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchAuctionService implements SearchAuctionSummaryUseCase {

    private final SearchAuctionRepository searchAuctionRepository;
    private final SearchItemRepository searchItemRepository;

    @Override
    public List<AuctionSummary> searchSummaries(SearchAuctionCommand command, List<Item> items) {
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        List<SearchAuctionSummaryResult> results = searchAuctionRepository.searchSummaries(command, itemMap.keySet());

        return results.stream()
                .filter(result -> itemMap.containsKey(result.itemId()))
                .map(summaryResult -> new AuctionSummary(itemMap.get(summaryResult.itemId()), summaryResult))
                .toList();
    }

    public List<Auction> findActiveAuctions(Long itemId, RegionType region, Long realmId) {
        return searchAuctionRepository.findActiveAuctions(itemId, region, realmId);
    }

}
