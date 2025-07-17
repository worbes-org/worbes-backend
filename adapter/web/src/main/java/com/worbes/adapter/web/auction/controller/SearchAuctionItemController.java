package com.worbes.adapter.web.auction.controller;

import com.worbes.adapter.web.auction.model.SearchAuctionItemRequest;
import com.worbes.application.auction.port.in.SearchAuctionItemQuery;
import com.worbes.application.auction.port.in.SearchAuctionItemResult;
import com.worbes.application.auction.port.in.SearchAuctionItemUseCase;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class SearchAuctionItemController {

    private final SearchItemUseCase searchItemUseCase;
    private final SearchAuctionItemUseCase searchAuctionItemUseCase;

    @GetMapping
    public Slice<SearchAuctionItemResult> getSummary(
            @Valid SearchAuctionItemRequest request,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("[AuctionSearch] Received request: {}", request);
        List<Item> items = searchItemUseCase.execute(new SearchItemQuery(request.classId(), request.subclassId(), request.name()));
        if (items.isEmpty()) return new SliceImpl<>(List.of(), pageable, false);

        List<SearchAuctionItemResult> result = searchAuctionItemUseCase.execute(
                        new SearchAuctionItemQuery(
                                request.region(),
                                request.realmId(),
                                items,
                                new PageInfo(pageable.getOffset(), pageable.getPageSize())
                        )
                ).stream()
                .toList();

        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result = result.subList(0, pageable.getPageSize());
        }
        log.info("[AuctionSearch] Returning {} results (hasNext={})", result.size(), hasNext);

        return new SliceImpl<>(result, pageable, hasNext);
    }
}
