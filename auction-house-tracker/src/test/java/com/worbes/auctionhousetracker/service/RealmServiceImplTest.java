package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.exception.UnauthorizedException;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.RealmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.TestUtils.loadJsonResource;
import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealmServiceImplTest {

    @Mock
    private RealmRepository realmRepository;

    @Mock
    private RestApiClient restApiClient;

    private RealmServiceImpl realmService;

    private Region region;
    private String realmIndexPath;
    private Map<String, String> realmIndexParams;
    private String realmSlug;
    private String realmPath;
    private Map<String, String> realmParams;

    @BeforeEach
    void setUp() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.initialize();

        realmService = new RealmServiceImpl(realmRepository, restApiClient, taskExecutor);

        // 테스트에 사용할 공통 변수 초기화
        region = Region.KR;
        realmIndexPath = BlizzardApiUrlBuilder.builder(region).realmIndex().build();
        realmIndexParams = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();
        realmSlug = "hyjal";
        realmPath = BlizzardApiUrlBuilder.builder(region).realm(realmSlug).build();
        realmParams = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();
    }

    @Test
    @DisplayName("저장된 서버 수 조회 - 성공")
    void count_ShouldReturnRepositoryCount() {
        // given
        given(realmRepository.count()).willReturn(10L);

        // when
        long count = realmService.count();

        // then
        assertThat(count).isEqualTo(10L);
        verify(realmRepository).count();
        verifyNoMoreInteractions(realmRepository);
    }

    @Test
    @DisplayName("서버 목록 저장 - 성공")
    void saveAll_ShouldCallRepositorySaveAll() {
        // given
        List<Realm> realms = List.of(Realm.builder().build(), Realm.builder().build());

        // when
        realmService.saveAll(realms);

        // then
        verify(realmRepository).saveAll(realms);
        verifyNoMoreInteractions(realmRepository);
    }

    @Test
    @DisplayName("서버 초기화 여부 - 초기화 안됨")
    void isRealmInitialized_ShouldReturnFalse_WhenNoRealmsSaved() {
        // given: 저장된 서버 수가 0인 경우
        given(realmRepository.count()).willReturn(0L);

        // when
        boolean initialized = realmService.isRealmInitialized();

        // then
        assertThat(initialized).isFalse();
        verify(realmRepository).count();
        verifyNoMoreInteractions(realmRepository);
    }

    @Test
    @DisplayName("서버 초기화 여부 - 초기화됨")
    void isRealmInitialized_ShouldReturnTrue_WhenRealmsSaved() {
        // given: 저장된 서버 수가 5 이상인 경우
        given(realmRepository.count()).willReturn(5L);

        // when
        boolean initialized = realmService.isRealmInitialized();

        // then
        assertThat(initialized).isTrue();
        verify(realmRepository).count();
        verifyNoMoreInteractions(realmRepository);
    }

    @Test
    @DisplayName("서버 목록 조회 - 성공")
    void fetchRealmIndexSuccess() {
        // given: JSON 리소스를 통해 기대하는 응답 객체 준비
        RealmIndexResponse expectedResponse = loadJsonResource("/json/realm-index-response.json", RealmIndexResponse.class);
        given(restApiClient.get(eq(realmIndexPath), eq(realmIndexParams), eq(RealmIndexResponse.class)))
                .willReturn(expectedResponse);

        // when: fetchRealmIndex 메서드 호출
        RealmIndexResponse response = realmService.fetchRealmIndex(region);

        // then: 응답 객체가 null이 아니고, 기대한 값과 일치하는지 검증
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);
        verify(restApiClient).get(eq(realmIndexPath), eq(realmIndexParams), eq(RealmIndexResponse.class));
        verifyNoMoreInteractions(restApiClient);
    }

    @Test
    @DisplayName("서버 목록 조회 - API 실패")
    void fetchRealmIndexWhenApiFails() {
        // given: API 호출 시 예외 발생하도록 설정
        given(restApiClient.get(eq(realmIndexPath), eq(realmIndexParams), eq(RealmIndexResponse.class)))
                .willThrow(new UnauthorizedException());

        // when & then: 메서드 호출 시 예외가 발생하는지 검증
        assertThatThrownBy(() -> realmService.fetchRealmIndex(region))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("서버 정보 조회 - 성공")
    void fetchRealmSuccess() {
        // given: JSON 리소스를 통해 RealmResponse 객체 준비
        RealmResponse realmResponse = loadJsonResource("/json/realm-response.json", RealmResponse.class);
        given(restApiClient.get(eq(realmPath), eq(realmParams), eq(RealmResponse.class)))
                .willReturn(realmResponse);

        // when: fetchRealm 메서드 호출
        Realm result = realmService.fetchRealm(region, realmSlug);

        // then: 결과 Realm 객체의 필드가 올바르게 설정되었는지 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(realmResponse.getId());
        assertThat(result.getName()).isEqualTo(realmResponse.getName());
        // connected realm ID는 URL에서 추출된 값이어야 함 (예제에서는 2116L로 예상)
        assertThat(result.getConnectedRealmId()).isEqualTo(2116L);
        verify(restApiClient).get(eq(realmPath), eq(realmParams), eq(RealmResponse.class));
        verifyNoMoreInteractions(restApiClient);
    }

    @Test
    @DisplayName("비동기 서버 정보 조회 - fetchRealm 호출 확인 (fetchRealmAsync)")
    void fetchRealmAsync_InvokesFetchRealm() {
        // given
        RealmResponse realmResponse = loadJsonResource("/json/realm-response.json", RealmResponse.class);
        given(restApiClient.get(eq(realmPath), eq(realmParams), eq(RealmResponse.class)))
                .willReturn(realmResponse);

        // spy를 사용해 fetchRealm 내부 호출을 감시하고, 결과를 stub 처리
        RealmServiceImpl spyService = spy(realmService);
        given(spyService.fetchRealm(region, realmSlug)).willReturn(mock(Realm.class));

        // when: 비동기 메서드 호출
        CompletableFuture<Realm> future = spyService.fetchRealmAsync(region, realmSlug);
        Realm actualRealm = future.join();

        // then: 내부적으로 fetchRealm이 호출되었는지와 결과가 예상대로 반환되는지 검증
        then(spyService).should(times(1)).fetchRealm(eq(region), eq(realmSlug));
    }

    @Test
    @DisplayName("서버 정보 조회 - API 실패")
    void fetchRealmWhenApiFails() {
        // given: API 호출 시 예외 발생하도록 설정
        given(restApiClient.get(eq(realmPath), eq(realmParams), eq(RealmResponse.class)))
                .willThrow(new UnauthorizedException());

        // when & then: 메서드 호출 시 예외가 발생하는지 검증
        assertThatThrownBy(() -> realmService.fetchRealm(region, realmSlug))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("복합 비동기 서버 데이터 조회 및 저장 - fetchRealmAsync 호출 및 saveAll 검증")
    void fetchAndSaveRealms_InvokesFetchRealmAsyncAndSaveAll() {
        // given
        Region region = Region.KR;

        // RealmIndexResponse에 포함될 서버 엔트리 두 개 생성
        RealmIndexResponse indexResponse = new RealmIndexResponse();
        RealmIndexResponse.RealmIndexElement element1 = new RealmIndexResponse.RealmIndexElement();
        element1.setSlug("realm1");
        RealmIndexResponse.RealmIndexElement element2 = new RealmIndexResponse.RealmIndexElement();
        element2.setSlug("realm2");
        indexResponse.setRealms(List.of(element1, element2));

        // 각 엔트리에 대해 반환될 Realm 객체 준비
        Realm realm1 = Realm.builder().build();
        Realm realm2 = Realm.builder().build();

        // spy를 사용해 fetchRealmIndex와 fetchRealmAsync를 stub 처리
        RealmServiceImpl spyService = spy(realmService);
        given(spyService.fetchRealmIndex(region)).willReturn(indexResponse);
        given(spyService.fetchRealmAsync(region, "realm1"))
                .willReturn(CompletableFuture.completedFuture(realm1));
        given(spyService.fetchRealmAsync(region, "realm2"))
                .willReturn(CompletableFuture.completedFuture(realm2));

        // when: 복합 비동기 메서드 호출 후 모든 작업 완료 대기
        CompletableFuture<Void> future = spyService.fetchAndSaveRealms(region);
        future.join();

        // then: saveAll() 호출 시 전달된 Realm 리스트에 realm1과 realm2가 포함되었는지 검증
        ArgumentCaptor<List<Realm>> captor = ArgumentCaptor.forClass(List.class);
        then(spyService).should().saveAll(captor.capture());
        List<Realm> savedRealms = captor.getValue();
        assertThat(savedRealms).containsExactlyInAnyOrder(realm1, realm2);
    }

}
