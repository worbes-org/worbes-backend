package com.worbes.adapter.jpa.realm;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.RealmQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "realm-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RealmReadRepositoryFindRealmSlugByRegionTest {

    @Autowired
    private RealmQueryRepository realmQueryRepository;

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

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("특정 region에 해당하는 slug만 반환된다")
        void findSlugByRegion_returnsOnlyMatchingRegion() {
            realmJpaRepository.save(createEntity(1L, 100L, RegionType.KR, "hyjal"));
            realmJpaRepository.save(createEntity(2L, 101L, RegionType.US, "illidan"));
            Set<String> result = realmQueryRepository.findSlugByRegion(RegionType.KR);
            assertThat(result).containsExactly("hyjal");
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("region이 여러 개일 때 slug가 region별로 구분된다")
        void sameSlugDifferentRegion() {
            realmJpaRepository.save(createEntity(1L, 100L, RegionType.KR, "hyjal"));
            realmJpaRepository.save(createEntity(2L, 101L, RegionType.US, "hyjal"));
            Set<String> krResult = realmQueryRepository.findSlugByRegion(RegionType.KR);
            Set<String> usResult = realmQueryRepository.findSlugByRegion(RegionType.US);
            assertThat(krResult).containsExactly("hyjal");
            assertThat(usResult).containsExactly("hyjal");
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("해당 region에 realm이 없으면 빈 Set을 반환한다")
        void findSlugByRegion_whenNoRealms_thenReturnsEmptySet() {
            realmJpaRepository.save(createEntity(1L, 100L, RegionType.US, "illidan"));
            Set<String> result = realmQueryRepository.findSlugByRegion(RegionType.KR);
            assertThat(result).isEmpty();
        }
    }
}
