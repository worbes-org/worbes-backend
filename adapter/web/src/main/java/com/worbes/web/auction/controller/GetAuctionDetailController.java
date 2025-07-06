package com.worbes.web.auction.controller;

import com.worbes.application.auction.model.AuctionDetail;
import com.worbes.application.auction.port.in.GetAuctionDetailUseCase;
import com.worbes.web.auction.model.GetAuctionDetailRequest;
import com.worbes.web.auction.model.GetAuctionDetailResponse;
import com.worbes.web.auction.model.ItemResponse;
import com.worbes.web.common.model.ApiResponse;
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
    private final GetAuctionDetailUseCase getAuctionDetailUseCase;

    @GetMapping("/{itemId}")
    public ApiResponse<GetAuctionDetailResponse> getAuctionDetail(
            @PathVariable("itemId") Long itemId,
            @Valid GetAuctionDetailRequest request
    ) {
        AuctionDetail auctionDetail = getAuctionDetailUseCase.getDetail(itemId, request.region(), request.realmId());

        return new ApiResponse<>(
                new GetAuctionDetailResponse(
                        new ItemResponse(auctionDetail.getItem()),
                        auctionDetail.getAvailable(),
                        auctionDetail.getTrends()
                )
        );
    }
}
