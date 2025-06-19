package com.worbes.adapter.jpa.repository.realm;

import com.worbes.adapter.jpa.entity.RealmEntity;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.CreateRealmRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Integration::CreateRealmRepository::SaveAll")
public class SaveAllTest {

    @Autowired
    private CreateRealmRepository createRealmRepository;

    @Autowired
    private RealmJpaRepository realmJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private static Realm createRealm(Long id, Long connectedRealmId, RegionType region, String slug) {
        return Realm.builder()
                .id(id)
                .connectedRealmId(connectedRealmId)
                .region(region)
                .slug(slug)
                .name(Map.of(LocaleCode.KO_KR.getValue(), "하이잘", LocaleCode.EN_US.getValue(), "Hyjal"))
                .build();
    }

    @Test
    @DisplayName("여러 Realm을 저장하고, 저장된 값들을 반환한다")
    void shouldSaveAllRealmsAndReturnSaved() {
        // given
        Realm realm1 = createRealm(1L, 101L, RegionType.KR, "hyjal");
        Realm realm2 = createRealm(2L, 101L, RegionType.KR, "azshara");

        List<Realm> realms = List.of(realm1, realm2);

        // when
        List<Realm> saved = createRealmRepository.saveAll(realms);
        entityManager.flush();
        entityManager.clear();

        // then
        assertThat(saved).hasSize(2);

        RealmEntity saved1 = realmJpaRepository.findById(1L).orElseThrow();
        RealmEntity saved2 = realmJpaRepository.findById(2L).orElseThrow();

        assertThat(saved1.getSlug()).isEqualTo("hyjal");
        assertThat(saved2.getSlug()).isEqualTo("azshara");

        assertThat(saved1.getRegion()).isEqualTo(RegionType.KR);
        assertThat(saved2.getConnectedRealmId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("중복 ID 포함 리스트 저장 시 정상 처리되는지 확인")
    void saveAll_withDuplicateIds() {
        // given
        Realm realm1 = createRealm(1L, 100L, RegionType.KR, "slug1");
        Realm realm2 = createRealm(1L, 101L, RegionType.US, "slug2"); // 중복 id 1L

        // when
        List<Realm> saved = createRealmRepository.saveAll(List.of(realm1, realm2));
        entityManager.flush();
        entityManager.clear();

        // then
        // DB 제약조건에 따라 다름: JPA는 기본적으로 덮어쓰기 가능 → 결과 확인
        assertThat(saved).hasSize(2);
        assertThat(saved.stream().map(Realm::getId)).containsExactlyInAnyOrder(1L, 1L);
    }

    @Test
    @DisplayName("필수 필드 누락(region == null) 시 예외 발생")
    void saveAll_withNullRegion_throwsException() {
        // given
        Realm realm = createRealm(1L, 100L, null, "slug1");

        // when & then
        assertThatThrownBy(() -> {
            createRealmRepository.saveAll(List.of(realm));
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("name 필드가 null 또는 빈맵일 때 저장 처리 결과 확인")
    void saveAll_withNullOrEmptyName() {
        // null name
        Realm nullNameRealm = Realm.builder()
                .id(1L)
                .connectedRealmId(100L)
                .region(RegionType.KR)
                .slug("slug1")
                .name(null)
                .build();
        // 빈맵 name
        Realm emptyNameRealm = Realm.builder()
                .id(2L)
                .connectedRealmId(101L)
                .region(RegionType.KR)
                .slug("slug2")
                .name(Map.of())
                .build();

        // when then
        assertThatThrownBy(() -> {
            createRealmRepository.saveAll(List.of(nullNameRealm, emptyNameRealm));
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("중복 region + slug 포함 시 unique constraint 위반으로 예외 발생")
    void saveAll_withDuplicateRegionSlug_throwsException() {
        // given
        Realm realm1 = createRealm(1L, 100L, RegionType.KR, "slug1");
        Realm realm2 = createRealm(2L, 101L, RegionType.KR, "slug1"); // 동일 region, slug 중복

        // when & then
        assertThatThrownBy(() -> {
            createRealmRepository.saveAll(List.of(realm1, realm2));
            entityManager.flush();  // 제약조건 검사 위해 flush 필요
        }).isInstanceOf(ConstraintViolationException.class);
    }
}
