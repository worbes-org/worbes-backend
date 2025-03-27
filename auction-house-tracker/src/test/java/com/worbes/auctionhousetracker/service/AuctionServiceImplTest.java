package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuctionServiceImplTest {

    @Mock
    AuctionRepository auctionRepository;

    @InjectMocks
    AuctionServiceImpl auctionService;

//    @ParameterizedTest
//    @EnumSource(Region.class)
//    @DisplayName("각 리전별로 올바른 URL과 파라미터로 경매장 데이터를 가져오는지 검증")
//    void fetchAuctions_ShouldBuildCorrectUrlAndParamsForEachRegion(Region region) {
//        // Given
//        AuctionResponse auctionResponse = loadJsonResource("/json/auction-response.json", AuctionResponse.class);
//        given(restApiClient.get(anyString(), anyMap(), eq(AuctionResponse.class))).willReturn(auctionResponse);
//
//        // When
//        AuctionResponse result = auctionService.fetchCommodities(region);
//
//        // Then
//        verify(restApiClient).get(
//                eq(BlizzardApiUrlBuilder.builder(region).commodities().build()),
//                eq(BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build()),
//                eq(AuctionResponse.class)
//        );
//        assertNotNull(result);
//        assertEquals(auctionResponse.getAuctions().size(), result.getAuctions().size());
//    }

    @Test
    @DisplayName("새로운 경매 목록에 없는 기존 경매는 비활성화되어야 한다")
    void updateAuctions_ShouldDeactivateAuctionsNotInNewList() {
        // Given
        Region region = Region.KR;
        Auction dummyAuction1 = Auction.builder()
                .auctionId(1L)
                .region(region)
                .build();
        Auction dummyAuction2 = Auction.builder()
                .auctionId(2L)
                .region(region)
                .build();
        given(auctionRepository.findByActiveTrueAndRegion(region))
                .willReturn(List.of(dummyAuction1, dummyAuction2));

        // When
        List<Auction> newAuctions = List.of(
                Auction.builder()
                        .auctionId(1L)
                        .region(region)
                        .build()
        );
        auctionService.updateAuctions(newAuctions, region);

        // Then
        assertFalse(dummyAuction2.isActive());
    }

    @Test
    @DisplayName("기존에 없던 새로운 경매만 저장되어야 한다")
    void updateAuctions_ShouldSaveOnlyNewAuctions() {
        // Given
        Region region = Region.KR;
        Auction existingAuction = Auction.builder()
                .auctionId(1L)
                .region(region)
                .build();
        given(auctionRepository.findByActiveTrueAndRegion(region))
                .willReturn(List.of(existingAuction));

        // When
        List<Auction> newAuctions = List.of(
                Auction.builder()
                        .auctionId(1L)
                        .region(region)
                        .build(),
                Auction.builder()
                        .auctionId(2L)
                        .region(region)
                        .build()
        );
        auctionService.updateAuctions(newAuctions, region);

        // Then
        verify(auctionRepository).saveAll(argThat(auctions -> {
            List<Auction> auctionList = StreamSupport.stream(auctions.spliterator(), false).toList();
            return auctionList.size() == 1 && auctionList.get(0).getAuctionId().equals(2L);
        }));
    }

    @Test
    @DisplayName("기존 활성화된 경매가 없는 경우 새로운 경매들이 모두 저장되어야 한다")
    void updateAuctions_WhenNoActiveAuctions_ShouldSaveAllNewAuctions() {
        // Given
        Region region = Region.KR;
        given(auctionRepository.findByActiveTrueAndRegion(region))
                .willReturn(List.of());

        List<Auction> newAuctions = List.of(
                Auction.builder()
                        .auctionId(1L)
                        .region(region)
                        .build(),
                Auction.builder()
                        .auctionId(2L)
                        .region(region)
                        .build()
        );

        // When
        auctionService.updateAuctions(newAuctions, region);

        // Then
        verify(auctionRepository).saveAll(argThat(auctions ->
                StreamSupport.stream(auctions.spliterator(), false).count() == 2
        ));
    }

    @Test
    @DisplayName("새로운 경매 데이터가 없는 경우 기존 경매가 모두 비활성화되어야 한다")
    void updateAuctions_WhenNoNewAuctions_ShouldDeactivateAllExistingAuctions() {
        // Given
        Region region = Region.KR;
        Auction auction1 = Auction.builder()
                .auctionId(1L)
                .region(region)
                .build();
        Auction auction2 = Auction.builder()
                .auctionId(2L)
                .region(region)
                .build();
        given(auctionRepository.findByActiveTrueAndRegion(region))
                .willReturn(List.of(auction1, auction2));

        // When
        auctionService.updateAuctions(List.of(), region);

        // Then
        assertFalse(auction1.isActive());
        assertFalse(auction2.isActive());
        verify(auctionRepository).saveAll(argThat(auctions ->
                StreamSupport.stream(auctions.spliterator(), false).findAny().isEmpty()
        ));
    }

    @Test
    @DisplayName("realmId가 있는 경우 해당 realm의 경매만 처리해야 한다")
    void updateAuctions_WithRealmId_ShouldOnlyProcessAuctionsForThatRealm() {
        // Given
        Region region = Region.KR;
        Long realmId = 1L;
        Auction existingAuction = Auction.builder()
                .auctionId(1L)
                .region(region)
                .realmId(realmId)
                .build();
        given(auctionRepository.findByActiveTrueAndRegionAndRealmId(region, realmId))
                .willReturn(List.of(existingAuction));

        // When
        List<Auction> newAuctions = List.of(
                Auction.builder()
                        .auctionId(2L)
                        .region(region)
                        .realmId(realmId)
                        .build()
        );
        auctionService.updateAuctions(newAuctions, region, realmId);

        // Then
        verify(auctionRepository).findByActiveTrueAndRegionAndRealmId(region, realmId);
    }

    @Test
    @DisplayName("입력값이 null인 경우 예외가 발생해야 한다")
    void updateAuctions_WithNullInputs_ShouldThrowException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () ->
                        auctionService.updateAuctions(null, Region.KR)),
                () -> assertThrows(IllegalArgumentException.class, () ->
                        auctionService.updateAuctions(List.of(), null))
        );
    }
}
