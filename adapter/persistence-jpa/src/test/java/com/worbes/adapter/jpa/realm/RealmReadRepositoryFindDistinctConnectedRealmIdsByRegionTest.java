package com.worbes.adapter.jpa.realm;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.FindRealmPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "realm-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RealmReadRepositoryFindDistinctConnectedRealmIdsByRegionTest {

    @Autowired
    private FindRealmPort findRealmPort;

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
        @DisplayName("특정 region의 connectedRealmId 목록을 중복 없이 조회한다")
        void findDistinctConnectedRealmIds_returnsUniqueIds() {
            // given
            realmJpaRepository.save(createEntity(1L, 100L, RegionType.KR, "hyjal"));
            realmJpaRepository.save(createEntity(2L, 101L, RegionType.KR, "azshara"));
            realmJpaRepository.save(createEntity(3L, 100L, RegionType.KR, "stormrage")); // 중복 connectedRealmId

            // when
            List<Long> result = findRealmPort.findDistinctConnectedRealmIdByRegion(RegionType.KR);

            // then
            assertThat(result).containsExactlyInAnyOrder(100L, 101L);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("다른 region의 connectedRealmId는 포함되지 않는다")
        void findDistinctConnectedRealmIds_excludesOtherRegions() {
            // given
            realmJpaRepository.save(createEntity(1L, 100L, RegionType.KR, "hyjal"));
            realmJpaRepository.save(createEntity(2L, 100L, RegionType.US, "hyjal")); // region 다름

            // when
            List<Long> result = realmJpaRepository.findDistinctConnectedRealmIdsByRegion(RegionType.KR);

            // then
            assertThat(result).containsExactly(100L);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("해당 region에 realm이 없으면 빈 리스트를 반환한다")
        void findDistinctConnectedRealmIds_returnsEmptyWhenNone() {
            // given
            realmJpaRepository.save(createEntity(1L, 100L, RegionType.US, "illidan"));

            // when
            List<Long> result = realmJpaRepository.findDistinctConnectedRealmIdsByRegion(RegionType.KR);

            // then
            assertThat(result).isEmpty();
        }
    }
}
