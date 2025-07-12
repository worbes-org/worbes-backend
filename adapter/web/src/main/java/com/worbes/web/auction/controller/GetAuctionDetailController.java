package com.worbes.web.auction.controller;

import com.worbes.application.auction.model.AuctionTrend;
import com.worbes.application.auction.port.in.GetActiveAuctionUseCase;
import com.worbes.application.auction.port.in.GetAuctionTrendUseCase;
import com.worbes.application.auction.port.out.AuctionSearchCondition;
import com.worbes.application.auction.port.out.AuctionTrendSearchCondition;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.GetItemUseCase;
import com.worbes.application.realm.model.RegionType;
import com.worbes.web.auction.model.GetAuctionDetailRequest;
import com.worbes.web.auction.model.GetAuctionDetailResponse;
import com.worbes.web.auction.model.ItemResponse;
import com.worbes.web.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GetAuctionDetailController {

    private final GetItemUseCase getItemUseCase;
    private final GetActiveAuctionUseCase getActiveAuctionUseCase;
    private final GetAuctionTrendUseCase getAuctionTrendUseCase;

    @GetMapping("/{itemId}")
    public ApiResponse<GetAuctionDetailResponse> getAuctionDetail(
            @PathVariable("itemId") Long itemId,
            @Valid GetAuctionDetailRequest request
    ) {
        Long realmId = request.realmId();
        RegionType region = request.region();
        String itemBonus = request.itemBonus();

        Item item = getItemUseCase.get(itemId);
        Map<Long, Integer> priceGroup = getActiveAuctionUseCase.groupActiveAuctionsByPrice(
                new AuctionSearchCondition(region, realmId, itemId, itemBonus)
        );
        AuctionTrend trend = getAuctionTrendUseCase.getHourlyTrend(
                new AuctionTrendSearchCondition(region, realmId, itemId, itemBonus, 7)
        );


        return new ApiResponse<>(
                new GetAuctionDetailResponse(
                        new ItemResponse(item, itemBonus),
                        priceGroup,
                        trend.getTrendPoints()
                )
        );
    }
}
