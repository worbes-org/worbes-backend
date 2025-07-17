package com.worbes.adapter.web.auction.controller;

import com.worbes.adapter.web.auction.model.GetAuctionDetailRequest;
import com.worbes.adapter.web.auction.model.GetAuctionDetailResponse;
import com.worbes.adapter.web.common.ApiResponse;
import com.worbes.application.auction.port.in.*;
import com.worbes.application.realm.model.RegionType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GetAuctionDetailController {

    private final GetAuctionItemStatsUseCase getAuctionItemStatsUseCase;
    private final GetAuctionItemTrendUseCase getAuctionItemTrendUseCase;

    @GetMapping("/{itemId}")
    public ApiResponse<GetAuctionDetailResponse> getAuctionDetail(
            @PathVariable("itemId") Long itemId,
            @Valid GetAuctionDetailRequest request
    ) {
        Long realmId = request.realmId();
        RegionType region = request.region();
        String itemBonus = request.itemBonus();

        GetAuctionItemStatsResult auctionItemStatsResult = getAuctionItemStatsUseCase.execute(
                new GetAuctionItemStatsQuery(region, realmId, itemId, itemBonus)
        );
        GetAuctionItemTrendResult auctionItemTrendResult = getAuctionItemTrendUseCase.execute(
                new GetAuctionItemTrendQuery(region, realmId, itemId, itemBonus, 14)
        );

        return new ApiResponse<>(new GetAuctionDetailResponse(auctionItemStatsResult, auctionItemTrendResult));
    }
}
