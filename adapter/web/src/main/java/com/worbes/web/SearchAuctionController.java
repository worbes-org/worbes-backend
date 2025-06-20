package com.worbes.web;

import com.worbes.application.auction.model.AuctionSummary;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchAllItemUseCase;
import com.worbes.application.item.port.in.SearchItemCommand;
import com.worbes.application.realm.model.RegionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class SearchAuctionController {

    private final SearchAllItemUseCase searchAllItemUseCase;
    private final SearchAuctionSummaryUseCase searchAuctionSummaryUseCase;

    @GetMapping
    public ApiResponse<List<SearchAuctionResponse>> searchAuction(@Valid SearchAuctionRequest request) {
        RegionType region = RegionType.valueOf(request.region());
        LocaleCode locale = LocaleCode.fromValue(request.locale());
        List<Item> items = searchAllItemUseCase.searchAll(
                new SearchItemCommand(
                        request.itemClassId(),
                        request.itemSubclassId(),
                        request.itemName()
                )
        );
        List<AuctionSummary> auctionSummaries = searchAuctionSummaryUseCase.searchSummaries(
                new SearchAuctionCommand(
                        region,
                        request.realmId(),
                        locale
                ),
                items
        );
        List<SearchAuctionResponse> result = auctionSummaries.stream()
                .map(as -> new SearchAuctionResponse(as, locale))
                .toList();

        return new ApiResponse<>(result);
    }
}
