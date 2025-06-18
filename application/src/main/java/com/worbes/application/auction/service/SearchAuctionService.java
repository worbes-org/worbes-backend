package com.worbes.application.auction.service;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.Item;
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

    @Override
    public List<AuctionSummary> searchSummaries(SearchAuctionCommand command, List<Item> items) {
        LocaleCode locale = command.locale();
        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        List<SearchAuctionSummaryResult> results = searchAuctionRepository.searchSummaries(command, itemMap.keySet());
        return results.stream()
                .map(result -> new AuctionSummary(
                                itemMap.get(result.itemId()).getName(locale),
                                result.lowestUnitPrice(),
                                result.lowestBuyout(),
                                result.available()
                        )
                ).toList();
    }
}
