package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.repository.RealmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RealmServiceImplTest {

    @Mock
    private RealmRepository realmRepository;

    @InjectMocks
    private RealmServiceImpl realmService;

    @DisplayName("DB와 API 모두 비어 있으면 빈 리스트 반환")
    @Test
    void getMissingRealmSlugs_ShouldReturnEmptyList_WhenBothDBAndAPIEmpty() {
        // Given
        given(realmRepository.findByRegion(Region.KR)).willReturn(List.of());
        RealmIndexResponse response = new RealmIndexResponse();
        response.setRealms(List.of()); // API도 비어 있음

        // When
        List<String> missingRealmIds = realmService.getMissingRealmSlugs(response, Region.KR);

        // Then
        assertThat(missingRealmIds).isEmpty();
    }

    @DisplayName("DB가 비어 있을 때 API에서 받은 모든 Realm ID를 반환")
    @Test
    void getMissingRealmSlugs_ShouldReturnAllRealmIds_WhenDBIsEmpty() {
        // Given
        given(realmRepository.findByRegion(Region.KR)).willReturn(List.of()); // DB 비어 있음

        RealmIndexResponse response = new RealmIndexResponse();
        response.setRealms(List.of(
                new RealmIndexResponse.RealmDto(1L, "azshara"),
                new RealmIndexResponse.RealmDto(2L, "alleria")
        ));

        // When
        List<String> missingRealmIds = realmService.getMissingRealmSlugs(response, Region.KR);

        // Then
        assertThat(missingRealmIds).containsExactly("azshara", "alleria");
    }

    @DisplayName("API에서 받은 Realm ID가 모두 DB에 존재하면 빈 리스트 반환")
    @Test
    void getMissingRealmSlugs_ShouldReturnEmptyList_WhenNoMissingRealms() {
        // Given
        given(realmRepository.findByRegion(Region.KR)).willReturn(List.of(
                Realm.builder().id(1L).connectedRealmId(100L).region(Region.KR).slug("azshara").build(),
                Realm.builder().id(2L).connectedRealmId(101L).region(Region.KR).slug("alleria").build()
        ));

        RealmIndexResponse response = new RealmIndexResponse();
        response.setRealms(List.of(
                new RealmIndexResponse.RealmDto(1L, "azshara"),
                new RealmIndexResponse.RealmDto(2L, "alleria")
        ));

        // When
        List<String> missingRealmIds = realmService.getMissingRealmSlugs(response, Region.KR);

        // Then
        assertThat(missingRealmIds).isEmpty();
    }

    @Test
    @DisplayName("API에서 받은 Realm 리스트 중 DB에 없는 Realm slug만 반환해야 한다 (Region 필터 적용)")
    void getMissingRealmSlugs_ShouldReturnMissingSlugsForRegion() {
        // Given: DB에 저장된 KR 서버 목록
        given(realmRepository.findByRegion(Region.KR)).willReturn(List.of(
                Realm.builder().id(1L).connectedRealmId(100L).region(Region.KR).slug("azshara").build(),
                Realm.builder().id(2L).connectedRealmId(101L).region(Region.KR).slug("alleria").build()
        ));

        // API에서 받은 Realm 데이터 (KR 서버)
        RealmIndexResponse response = new RealmIndexResponse();
        response.setRealms(List.of(
                new RealmIndexResponse.RealmDto(1L, "azshara"), // DB에 있음
                new RealmIndexResponse.RealmDto(2L, "alleria"), // DB에 있음
                new RealmIndexResponse.RealmDto(3L, "blackhand") // DB에 없음
        ));

        // When: getMissingRealmIds 호출
        List<String> missingRealmIds = realmService.getMissingRealmSlugs(response, Region.KR);

        // Then: DB에 없는 ID(3)만 리턴되어야 함
        assertThat(missingRealmIds).containsExactly("blackhand");
    }
}
