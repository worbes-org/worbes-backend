package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import com.worbes.auctionhousetracker.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties.*;
import static com.worbes.auctionhousetracker.utils.TestUtils.createRandomAuctionDtos;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuctionServiceImplTest {

    @Mock
    AuctionRepository auctionRepository;

    @Mock
    RestApiClient restApiClient;

    @InjectMocks
    AuctionServiceImpl auctionService;

    @ParameterizedTest
    @EnumSource(Region.class)
    @DisplayName("각 리전별로 올바른 URL과 파라미터로 경매장 데이터를 가져오는지 검증")
    void fetchAuctions_ShouldBuildCorrectUrlAndParamsForEachRegion(Region region) {
        // Given
        AuctionResponse mockedResponse = mock(AuctionResponse.class);
        given(mockedResponse.getAuctions()).willReturn(createRandomAuctionDtos(10));
        given(restApiClient.get(anyString(), anyMap(), eq(AuctionResponse.class))).willReturn(mockedResponse);

        // When
        List<Auction> result = auctionService.fetchAuctions(region);

        // Then
        Map<String, String> expectedParams = Map.of(NAMESPACE_KEY, String.format(NAMESPACE_DYNAMIC, region.getValue()));
        String expectedBaseUrl = String.format(BASE_URL, region.getValue());
        verify(restApiClient).get(
                eq(String.format(expectedBaseUrl.concat(COMMODITIES_URL), region.getValue())),
                eq(expectedParams),
                eq(AuctionResponse.class)
        );
        assertNotNull(result);
        assertEquals(mockedResponse.getAuctions().size(), result.size());
    }

    @Test
    void saveAuctions_ShouldSaveFetchedAuctionsToDatabase() {
        // Given
        List<Auction> auctions = createRandomAuctionDtos(100000)
                .stream()
                .map(dto -> new Auction(dto, Region.KR))
                .toList();

        // When
        auctionService.saveAuctions(auctions);

        // Then
        verify(auctionRepository, times(1)).saveAll(auctions); // ✅ DB에 저장됐는지 검증
    }
}
