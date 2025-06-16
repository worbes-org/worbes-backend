package com.worbes.application.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.CreateRealmRepository;
import com.worbes.application.realm.port.out.FindRealmRepository;
import com.worbes.application.realm.port.out.RealmFetchResult;
import com.worbes.application.realm.port.out.RealmFetcher;
import com.worbes.application.realm.service.InitializeRealmService;
import com.worbes.application.realm.service.RealmFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InitializeRealmServiceTest {

    @Mock
    private CreateRealmRepository createRealmRepository;

    @Mock
    private FindRealmRepository findRealmRepository;

    @Mock
    private RealmFetcher realmFetcher;

    @Mock
    private RealmFactory realmFactory;

    @InjectMocks
    private InitializeRealmService service;

    @Test
    @DisplayName("initialize: 존재하지 않는 realm 을 fetch 해서 저장한다")
    void initialize_shouldFetchAndSaveMissingRealms() {
        // given
        RegionType region = RegionType.KR;

        Set<String> allSlugs = Set.of("burning-legion", "frostmourne");
        Set<String> existingSlugs = Set.of();

        RealmFetchResult dto1 = createDto(1L, "burning-legion");
        RealmFetchResult dto2 = createDto(2L, "frostmourne");

        Realm expected1 = createRealmFromDto(dto1);
        Realm expected2 = createRealmFromDto(dto2);

        given(realmFetcher.fetchRealmIndex(region)).willReturn(allSlugs);
        given(findRealmRepository.findSlugByRegion(region)).willReturn(existingSlugs);

        given(realmFetcher.fetchAllRealmsAsync(region, allSlugs))
                .willReturn(CompletableFuture.completedFuture(List.of(dto1, dto2)));

        given(realmFactory.create(dto1)).willReturn(expected1);
        given(realmFactory.create(dto2)).willReturn(expected2);

        given(createRealmRepository.saveAll(List.of(expected1, expected2))).willReturn(List.of(expected1, expected2));

        // when
        List<Realm> result = service.initialize(region);

        // then
        assertThat(result).containsExactlyInAnyOrder(expected1, expected2);

        // and: verify call order
        verify(realmFetcher).fetchRealmIndex(region);
        verify(findRealmRepository).findSlugByRegion(region);
        verify(realmFetcher).fetchAllRealmsAsync(region, allSlugs);
        verify(createRealmRepository).saveAll(List.of(expected1, expected2));
    }

    private RealmFetchResult createDto(Long id, String slug) {
        return new RealmFetchResult(id, Map.of("en_US", slug), "", slug, RegionType.KR);
    }

    private Realm createRealmFromDto(RealmFetchResult dto) {
        return Realm.builder()
                .id(dto.id())
                .name(dto.name())
                .region(dto.region())
                .region(dto.region())
                .slug(dto.slug())
                .connectedRealmId(dto.id() + 100)
                .build();
    }
}
