package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.service.RealmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.worbes.auctionhousetracker.TestUtils.loadJsonResource;
import static com.worbes.auctionhousetracker.service.RealmServiceImpl.extractIdFromUrl;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealmDataInitializerTest {

    @Mock
    private RealmService realmService;

    @InjectMocks
    private RealmDataInitializer realmDataInitializer;

    @Test
    @DisplayName("서버 데이터 초기화 - 이미 데이터가 존재하는 경우")
    void runWhenDataAlreadyExists() throws Exception {
        // given
        given(realmService.count()).willReturn(10L);

        // when
        realmDataInitializer.run();

        // then
        verify(realmService, times(1)).count();
        verify(realmService, never()).fetchRealmIndex(any());
        verify(realmService, never()).fetchRealm(any(), any());
        verify(realmService, never()).saveAll(any());
    }

    @Test
    @DisplayName("서버 데이터 초기화 - 성공")
    void runSuccess() throws Exception {
        // given
        RealmIndexResponse realmIndexResponse = loadJsonResource("/json/realm-index-response.json", RealmIndexResponse.class);
        RealmResponse realmResponse = loadJsonResource("/json/realm-response.json", RealmResponse.class);
        Realm realm = Realm.builder()
                .id(realmResponse.getId())
                .name(realmResponse.getName())
                .connectedRealmId(extractIdFromUrl(realmResponse.getConnectedRealmHref()))
                .region(Region.KR)
                .build();

        given(realmService.count()).willReturn(0L);
        given(realmService.fetchRealmIndex(any(Region.class))).willReturn(realmIndexResponse);
        given(realmService.fetchRealm(any(Region.class), any())).willReturn(realm);

        // when
        realmDataInitializer.run();

        // then
        verify(realmService, times(1)).count();
        verify(realmService, times(1)).fetchRealmIndex(any(Region.class));
        verify(realmService, times(realmIndexResponse.getRealms().size())).fetchRealm(any(Region.class), any());
        verify(realmService, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("서버 데이터 초기화 - API 실패")
    void runWhenApiFails() throws Exception {
        // given
        given(realmService.count()).willReturn(0L);
        given(realmService.fetchRealmIndex(Region.KR))
                .willThrow(new RuntimeException("API 호출 실패"));

        // when & then
        assertThatThrownBy(() -> realmDataInitializer.run())
                .isInstanceOf(RuntimeException.class);
        //TODO 예외 처리 수정
    }
}
