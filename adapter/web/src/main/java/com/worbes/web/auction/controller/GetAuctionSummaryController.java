package com.worbes.web.auction.controller;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.in.GetAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.AuctionSummarySearchCondition;
import com.worbes.application.common.model.PageInfo;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.GetItemUseCase;
import com.worbes.application.item.port.out.ItemSearchCondition;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GetAuctionSummaryController {

    private final GetItemUseCase getItemUseCase;
    private final GetAuctionSummaryUseCase getAuctionSummaryUseCase;

    @GetMapping
    public Slice<SearchAuctionSummaryResponse> getSummary(
            @Valid SearchAuctionRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("[AuctionSearch] Received request: {}", request);
        List<Item> items = executeSearchItemUseCase(request);
        if (items.isEmpty()) return new SliceImpl<>(List.of(), pageable, false);

        List<AuctionSummary> auctionSummaries = executeSearchAuctionSummaryUseCase(request, pageable, items);
        List<SearchAuctionSummaryResponse> result = auctionSummaries.stream()
                .map(SearchAuctionSummaryResponse::new)
                .toList();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result = result.subList(0, pageable.getPageSize());
        }
        log.info("[AuctionSearch] Returning {} results (hasNext={})", result.size(), hasNext);

        return new SliceImpl<>(result, pageable, hasNext);
    }

    private List<Item> executeSearchItemUseCase(SearchAuctionRequest request) {
        return getItemUseCase.get(
                new ItemSearchCondition(
                        request.classId(),
                        request.subclassId(),
                        request.name()
                )
        );
    }

    private List<AuctionSummary> executeSearchAuctionSummaryUseCase(
            SearchAuctionRequest request,
            Pageable pageable,
            List<Item> items
    ) {
        return getAuctionSummaryUseCase.getSummary(
                new AuctionSummarySearchCondition(
                        request.region(),
                        request.realmId(),
                        items,
                        new PageInfo(pageable.getOffset(), pageable.getPageSize())
                )
        );
    }
}
