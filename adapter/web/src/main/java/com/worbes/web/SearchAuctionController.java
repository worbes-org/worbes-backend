package com.worbes.web;

import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchAllItemUseCase;
import com.worbes.application.item.port.in.SearchItemCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class SearchAuctionController {

    private final SearchAllItemUseCase searchAllItemUseCase;
    private final SearchAuctionSummaryUseCase searchAuctionSummaryUseCase;

    @GetMapping
    public ApiResponse<List<SearchAuctionResponse>> searchAuction(@Valid SearchAuctionRequest request) {
        List<Item> items = searchAllItemUseCase.searchAll(
                new SearchItemCommand(
                        request.itemClassId(),
                        request.itemSubclassId(),
                        request.itemName()
                )
        );
        Map<Item, SearchAuctionSummaryResult> summaries = searchAuctionSummaryUseCase.searchSummaries(
                new SearchAuctionCommand(
                        request.region(),
                        request.realmId(),
                        request.locale()
                ),
                items
        );
        List<SearchAuctionResponse> result = summaries.entrySet().stream()
                .map(entry -> new SearchAuctionResponse(
                                entry.getKey(),
                                entry.getValue(),
                                request.locale()
                        )
                ).toList();

        return new ApiResponse<>(result);
    }
}
