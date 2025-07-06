package com.worbes.adapter.jpa.realm;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.RealmWriteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "realm-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RealmWriteRepositorySaveAllRealmTest {

    @Autowired
    private RealmWriteRepository realmWriteRepository;

    @Autowired
    private RealmJpaRepository realmJpaRepository;

    private static Realm createRealm(Long id, Long connectedRealmId, RegionType region, String slug) {
        return Realm.builder()
                .id(id)
                .connectedRealmId(connectedRealmId)
                .region(region)
                .slug(slug)
                .name(Map.of(LocaleCode.KO_KR.getValue(), "하이잘", LocaleCode.EN_US.getValue(), "Hyjal"))
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("여러 Realm을 저장하고, 저장된 값들을 반환한다")
        void shouldSaveAllRealmsAndReturnSaved() {
            Realm realm1 = createRealm(1L, 101L, RegionType.KR, "hyjal");
            Realm realm2 = createRealm(2L, 101L, RegionType.KR, "azshara");
            List<Realm> saved = realmWriteRepository.saveAll(Set.of(realm1, realm2));
            assertThat(saved).hasSize(2);
            RealmEntity saved1 = realmJpaRepository.findById(1L).orElseThrow();
            RealmEntity saved2 = realmJpaRepository.findById(2L).orElseThrow();
            assertThat(saved1.getSlug()).isEqualTo("hyjal");
            assertThat(saved2.getSlug()).isEqualTo("azshara");
            assertThat(saved1.getRegion()).isEqualTo(RegionType.KR);
            assertThat(saved2.getConnectedRealmId()).isEqualTo(101L);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("필수 필드 누락(region == null) 시 예외 발생")
        void saveAll_withNullRegion_throwsException() {
            Realm realm = createRealm(1L, 100L, null, "slug1");
            assertThatThrownBy(() -> {
                realmWriteRepository.saveAll(Set.of(realm));
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("name 필드가 null 또는 빈맵일 때 저장 처리 결과 확인")
        void saveAll_withNullOrEmptyName() {
            Realm nullNameRealm = Realm.builder()
                    .id(1L)
                    .connectedRealmId(100L)
                    .region(RegionType.KR)
                    .slug("slug1")
                    .name(null)
                    .build();
            Realm emptyNameRealm = Realm.builder()
                    .id(2L)
                    .connectedRealmId(101L)
                    .region(RegionType.KR)
                    .slug("slug2")
                    .name(Map.of())
                    .build();
            assertThatThrownBy(() -> {
                realmWriteRepository.saveAll(Set.of(nullNameRealm, emptyNameRealm));
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("중복 region + slug 포함 시 unique constraint 위반으로 예외 발생")
        void saveAll_withDuplicateRegionSlug_throwsException() {
            Realm realm1 = createRealm(1L, 100L, RegionType.KR, "slug1");
            Realm realm2 = createRealm(2L, 101L, RegionType.KR, "slug1"); // 동일 region, slug 중복
            assertThatThrownBy(() -> {
                realmWriteRepository.saveAll(Set.of(realm1, realm2));
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
