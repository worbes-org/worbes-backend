package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.RealmEntity;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.FindRealmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Integration::FindRealmRepository::findSlugByRegion 통합 테스트")
@Transactional
@Rollback
public class RealmFindSlugByRegionTest {

    @Autowired
    private FindRealmRepository findRealmRepository;

    @Autowired
    private RealmJpaRepository realmJpaRepository;

    private RealmEntity createEntity(Long id, Long connectedRealmId, RegionType region, String slug) {
        return RealmEntity.builder()
                .id(id)
                .connectedRealmId(connectedRealmId)
                .region(region)
                .slug(slug)
                .name(Map.of("ko_kr", "하이잘", "en_us", "Hyjal"))
                .build();
    }

    @Test
    @DisplayName("특정 region에 해당하는 slug만 반환된다")
    void findSlugByRegion_returnsOnlyMatchingRegion() {
        // given
        realmJpaRepository.save(createEntity(1L, 100L, RegionType.KR, "hyjal"));
        realmJpaRepository.save(createEntity(2L, 101L, RegionType.US, "illidan"));

        // when
        Set<String> result = findRealmRepository.findSlugByRegion(RegionType.KR);

        // then
        assertThat(result).containsExactly("hyjal");
    }

    @Test
    @DisplayName("해당 region에 realm이 없으면 빈 Set을 반환한다")
    void findSlugByRegion_whenNoRealms_thenReturnsEmptySet() {
        // given
        realmJpaRepository.save(createEntity(1L, 100L, RegionType.US, "illidan"));

        // when
        Set<String> result = findRealmRepository.findSlugByRegion(RegionType.KR);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("region이 다른 경우 slug가 조회되지 않는다")
    void findSlugByRegion_ignoresOtherRegions() {
        // given
        realmJpaRepository.save(createEntity(1L, 100L, RegionType.KR, "hyjal"));
        realmJpaRepository.save(createEntity(2L, 101L, RegionType.US, "hyjal")); // slug는 같지만 region 다름

        // when
        Set<String> result = findRealmRepository.findSlugByRegion(RegionType.KR);

        // then
        assertThat(result).containsExactly("hyjal");
    }
}
