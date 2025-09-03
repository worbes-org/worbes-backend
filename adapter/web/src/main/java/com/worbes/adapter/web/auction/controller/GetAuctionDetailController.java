package com.worbes.adapter.web.auction.controller;

import com.worbes.adapter.web.auction.model.GetAuctionDetailRequest;
import com.worbes.adapter.web.auction.model.GetAuctionDetailResponse;
import com.worbes.adapter.web.auction.model.GetAuctionTrendResponse;
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

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GetAuctionDetailController {

    private final GetAuctionDetailUseCase getAuctionDetailUseCase;
    private final GetAuctionTrendUseCase getAuctionTrendUseCase;

    @GetMapping("/{itemId}")
    public ApiResponse<GetAuctionDetailResponse> getAuctionDetail(
            @PathVariable("itemId") Long itemId,
            @Valid GetAuctionDetailRequest request
    ) {
        RegionType region = request.region();
        Long realmId = request.realmId();
        List<Long> itemBonus = convertItemBonus(request.itemBonus());

        GetAuctionDetailResult getAuctionDetailResult = getAuctionDetailUseCase.execute(
                new GetAuctionDetailQuery(region, realmId, itemId, itemBonus)
        );
        GetAuctionTrendResult getAuctionTrendResult = getAuctionTrendUseCase.execute(
                new GetAuctionTrendQuery(region, realmId, itemId, itemBonus, 14)
        );

        GetAuctionTrendResponse getAuctionTrendResponse = new GetAuctionTrendResponse(
                getAuctionTrendResult.averageLowestPrice(),
                getAuctionTrendResult.medianLowestPrice(),
                getAuctionTrendResult.trendPoints()
                        .stream()
                        .map(GetAuctionTrendResponse.AuctionTrendDto::new)
                        .toList()
        );

        return new ApiResponse<>(
                new GetAuctionDetailResponse(
                        itemId,
                        request.itemBonus(),
                        getAuctionDetailResult.lowestPrice(),
                        getAuctionDetailResult.totalQuantity(),
                        getAuctionDetailResult.quantityByPrice(),
                        getAuctionTrendResponse
                )
        );
    }

    private List<Long> convertItemBonus(String itemBonus) {
        if (itemBonus == null || itemBonus.isBlank()) {
            return null;
        }

        try {
            return Arrays.stream(itemBonus.split(":"))
                    .filter(s -> !s.isBlank())
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("itemBonus 형식이 잘못되었습니다.");
        }
    }
}
