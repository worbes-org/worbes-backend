package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuctionServiceImplTest {

    @Mock
    RestApiClient restApiClient;

    @InjectMocks
    AuctionServiceImpl auctionService;

    @ParameterizedTest
    @EnumSource(Region.class)
    void fetchCommodities_ShouldBuildCorrectRequest(Region region) {
        // Given
        AuctionResponse mock = mock(AuctionResponse.class);
        given(restApiClient.get(anyString(), anyMap(), eq(AuctionResponse.class))).willReturn(mock);

        // When
        auctionService.fetchAuctions(region);

        // Then
        verify(restApiClient).get(
                eq(String.format("https://%s.api.blizzard.com/data/wow/auctions/commodities", region.getValue())),
                eq(Map.of("namespace", String.format("dynamic-%s", region.getValue()))),
                eq(AuctionResponse.class)
        );
    }
}
