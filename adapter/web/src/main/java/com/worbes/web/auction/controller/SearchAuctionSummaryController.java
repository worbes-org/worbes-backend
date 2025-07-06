package com.worbes.web.auction.controller;

import com.worbes.application.auction.port.in.SearchAuctionSummaryCondition;
import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.AuctionSummary;
import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemCondition;
import com.worbes.application.item.port.in.SearchItemUseCase;
import com.worbes.web.auction.model.SearchAuctionRequest;
import com.worbes.web.auction.model.SearchAuctionSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class SearchAuctionSummaryController {

    private final SearchItemUseCase searchItemUseCase;
    private final SearchAuctionSummaryUseCase searchAuctionSummaryUseCase;

    @GetMapping
    public Slice<SearchAuctionSummaryResponse> searchAuction(
            @Valid SearchAuctionRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("[AuctionSearch] Received request: {}", request);
        Map<Long, Item> items = searchItemUseCase(request);
        if (items.isEmpty()) {
            return new SliceImpl<>(List.of(), pageable, false);
        }
        List<AuctionSummary> auctionSummaries = searchAuctionSummaryUseCase.searchSummaries(
                new SearchAuctionSummaryCondition(
                        request.region(),
                        request.realmId(),
                        items.keySet(),
                        new PageInfo(pageable.getOffset(), pageable.getPageSize())
                )
        );
        List<SearchAuctionSummaryResponse> result = auctionSummaries.stream()
                .map(auctionSummary -> new SearchAuctionSummaryResponse(items.get(auctionSummary.itemId()), auctionSummary))
                .toList();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result = result.subList(0, pageable.getPageSize());
        }
        log.info("[AuctionSearch] Returning {} results (hasNext={})", result.size(), hasNext);

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private Map<Long, Item> searchItemUseCase(SearchAuctionRequest request) {
        List<Item> items = searchItemUseCase.search(
                new SearchItemCondition(
                        request.itemClassId(),
                        request.itemSubclassId(),
                        request.itemName()
                )
        );
        return items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
    }
}
