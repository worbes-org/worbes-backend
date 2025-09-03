package com.worbes.adapter.web.auction.controller;

import com.worbes.adapter.web.auction.model.SearchAuctionRequest;
import com.worbes.adapter.web.auction.model.SearchAuctionResponse;
import com.worbes.application.auction.port.in.SearchAuctionSummaryQuery;
import com.worbes.application.auction.port.in.SearchAuctionUseCase;
import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemQuery;
import com.worbes.application.item.port.in.SearchItemUseCase;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class SearchAuctionController {

    private final SearchItemUseCase searchItemUseCase;
    private final SearchAuctionUseCase searchAuctionUseCase;

    @GetMapping
    public Slice<SearchAuctionResponse> getSummary(
            @Valid SearchAuctionRequest request,
            @PageableDefault(size = 100) Pageable pageable
    ) {
        log.info("[AuctionSearch] Received request: {}", request);
        List<Item> items = searchItemUseCase.execute(
                new SearchItemQuery(
                        request.classId(),
                        request.subclassId(),
                        request.name(),
                        request.minQuality(),
                        request.maxQuality(),
                        request.expansionId()
                )
        );
        if (items.isEmpty()) return new SliceImpl<>(List.of(), pageable, false);

        List<SearchAuctionResponse> result = searchAuctionUseCase.execute(
                        new SearchAuctionSummaryQuery(
                                request.region(),
                                request.realmId(),
                                items,
                                request.minItemLevel(),
                                request.maxItemLevel(),
                                new PageInfo(pageable.getOffset(), pageable.getPageSize())
                        )
                )
                .stream()
                .map(r -> new SearchAuctionResponse(
                                r.itemId(),
                                convertItemBonus(r.itemBonus()),
                                r.itemLevel(),
                                r.craftingTier(),
                                r.lowestPrice(),
                                r.totalQuantity()
                        )
                )
                .toList();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result = result.subList(0, pageable.getPageSize());
        }
        log.info("[AuctionSearch] Returning {} results (hasNext={})", result.size(), hasNext);

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private String convertItemBonus(List<Long> itemBonusList) {
        if (itemBonusList == null || itemBonusList.isEmpty()) {
            return null;
        }

        return itemBonusList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(":"));
    }
}
