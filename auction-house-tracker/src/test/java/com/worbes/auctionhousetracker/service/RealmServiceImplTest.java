package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

    @DisplayName("API 응답이 빈 리스트일 때, 기존 DB는 영향을 받지 않아야 한다")
    @Test
    void getMissingRealmSlugs_ShouldReturnEmptyList_WhenApiResponseIsEmpty() {
        // Given: DB에는 데이터가 있지만 API는 빈 리스트 반환
        given(realmRepository.findByRegion(Region.KR)).willReturn(List.of(
                Realm.builder().id(1L).connectedRealmId(100L).region(Region.KR).slug("azshara").build()
        ));

        RealmIndexResponse response = new RealmIndexResponse();
        response.setRealms(List.of()); // API 응답이 빈 리스트

        // When
        List<String> missingRealmSlugs = realmService.getMissingRealmSlugs(response, Region.KR);

        // Then
        assertThat(missingRealmSlugs).isEmpty();
    }

    @DisplayName("올바른 URL에서 ID를 추출하고 Realm을 저장해야 한다")
    @Test
    void save_ShouldExtractIdFromUrlAndStoreRealm_WhenValidUrlGiven() {
        // Given
        Region region = Region.KR;
        Map<String, String> nameAzshara = Map.of("ko_KR", "아즈샤라", "en_US", "Azshara");

        List<RealmResponse> responses = List.of(
                new RealmResponse(1L, nameAzshara, "https://someurl.com/connected-realm/100", "azshara")
        );

        // When
        realmService.save(region, responses);

        // Then
        verify(realmRepository, times(1)).saveAll(argThat(realms -> {
            List<Realm> realmList = List.copyOf((java.util.Collection<? extends Realm>) realms); // ✅ Iterable → List 변환
            return realmList.size() == 1 &&
                    realmList.get(0).getId().equals(1L) &&
                    realmList.get(0).getConnectedRealmId().equals(100L) && // ✅ URL에서 추출된 ID 확인
                    realmList.get(0).getSlug().equals("azshara") &&
                    realmList.get(0).getName().equals(nameAzshara);
        }));
    }

    @DisplayName("여러 개의 Realm을 동시에 저장할 때 모두 저장되어야 한다")
    @Test
    void save_ShouldStoreMultipleRealms_WhenValidListGiven() {
        // Given
        Region region = Region.KR;
        Map<String, String> name1 = Map.of("ko_KR", "굴단", "en_US", "Gul'dan");
        Map<String, String> name2 = Map.of("ko_KR", "데스윙", "en_US", "Deathwing");

        List<RealmResponse> responses = List.of(
                new RealmResponse(4L, name1, "https://someurl.com/connected-realm/200", "guldan"),
                new RealmResponse(5L, name2, "https://someurl.com/connected-realm/201", "deathwing")
        );

        // When
        realmService.save(region, responses);

        // Then
        verify(realmRepository, times(1)).saveAll(argThat(realms -> {
            List<Realm> realmList = List.copyOf((java.util.Collection<? extends Realm>) realms);
            return realmList.size() == 2 &&
                    realmList.get(0).getSlug().equals("guldan") &&
                    realmList.get(1).getSlug().equals("deathwing");
        }));
    }

    @DisplayName("connectedRealmHref가 null이면 예외를 던져야 한다")
    @Test
    void save_ShouldThrowException_WhenConnectedRealmHrefIsNull() {
        // Given
        Region region = Region.KR;
        Map<String, String> nameAzshara = Map.of("ko_KR", "아즈샤라", "en_US", "Azshara");

        List<RealmResponse> responses = List.of(
                new RealmResponse(1L, nameAzshara, null, "azshara")
        );

        // When & Then
        assertThatThrownBy(() -> realmService.save(region, responses))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL must not be null or empty");
    }

    @DisplayName("빈 리스트를 입력하면 저장하지 않아야 한다")
    @Test
    void save_ShouldNotStoreRealms_WhenEmptyListGiven() {
        // Given
        Region region = Region.KR;
        List<RealmResponse> responses = List.of(); // 빈 리스트

        // When
        realmService.save(region, responses);

        // Then
        verify(realmRepository, never()).saveAll(any());
    }

    @DisplayName("유효하지 않은 URL이면 예외를 던진다")
    @Test
    void save_ShouldThrowException_WhenInvalidUrlGiven() {
        // Given
        Region region = Region.KR;
        Map<String, String> nameAzshara = Map.of("ko_KR", "아즈샤라", "en_US", "Azshara");

        List<RealmResponse> responses = List.of(
                new RealmResponse(1L, nameAzshara, "invalid_url", "azshara")
        );

        // When & Then
        assertThatThrownBy(() -> realmService.save(region, responses))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid URL format");
    }

    @DisplayName("URL의 마지막 부분이 숫자가 아니면 예외를 던져야 한다")
    @Test
    void extractIdFromUrl_ShouldThrowException_WhenUrlDoesNotEndWithNumber() {
        // Given
        String urlWithInvalidId = "https://example.com/connected-realm/invalid";
        Region region = Region.KR;
        Map<String, String> nameAzshara = Map.of("ko_KR", "아즈샤라", "en_US", "Azshara");

        List<RealmResponse> responses = List.of(
                new RealmResponse(1L, nameAzshara, urlWithInvalidId, "azshara")
        );

        // When & Then
        assertThatThrownBy(() -> realmService.save(region, responses))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ID format in URL");
    }

    @DisplayName("URL이 슬래시로 끝나도 ID를 정상적으로 추출해야 한다")
    @Test
    void extractIdFromUrl_ShouldExtractId_WhenUrlEndsWithSlash() {
        // Given
        String urlWithSlash = "https://example.com/connected-realm/67890/";
        Region region = Region.KR;
        Map<String, String> nameAzshara = Map.of("ko_KR", "아즈샤라", "en_US", "Azshara");

        List<RealmResponse> responses = List.of(
                new RealmResponse(1L, nameAzshara, urlWithSlash, "azshara")
        );

        // When
        realmService.save(region, responses);

        // Then
        verify(realmRepository, times(1)).saveAll(any());
    }


}
