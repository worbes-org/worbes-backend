package com.worbes.adapter.jpa.realm;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.RealmReadRepository;
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
class RealmReadRepositoryFindRealmByRegionTest {

    @Autowired
    private RealmReadRepository realmReadRepository;

    @Autowired
    private RealmJpaRepository realmJpaRepository;

    private RealmEntity createEntity(Long id, String slug, Long connectedRealmId, RegionType region, Map<String, String> name) {
        return RealmEntity.builder()
                .id(id)
                .slug(slug)
                .connectedRealmId(connectedRealmId)
                .name(name)
                .region(region)
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("region에 해당하는 RealmEntity들을 찾아 Realm 도메인으로 반환한다")
        void findByRegion_returnsRealms() {
            // given
            RegionType kr = RegionType.KR;
            RegionType us = RegionType.US;
            RealmEntity realm1 = createEntity(1L, "azshara", 1001L, kr, Map.of("ko_KR", "아즈샤라", "en_US", "Azshara"));
            RealmEntity realm2 = createEntity(2L, "stormrage", 1002L, kr, Map.of("ko_KR", "스톰레이지", "en_US", "Stormrage"));
            RealmEntity realm3 = createEntity(3L, "rexxar", 1003L, us, Map.of("ko_KR", "렉사르", "en_US", "Rexxar"));
            realmJpaRepository.saveAll(List.of(realm1, realm2, realm3));

            // when
            List<Realm> result1 = realmReadRepository.findByRegion(kr);
            List<Realm> result2 = realmReadRepository.findByRegion(us);

            // then
            assertThat(result1).hasSize(2);
            assertThat(result1)
                    .extracting(Realm::getSlug)
                    .containsExactlyInAnyOrder("azshara", "stormrage");

            assertThat(result2).hasSize(1);
            assertThat(result2)
                    .extracting(Realm::getSlug)
                    .containsExactlyInAnyOrder("rexxar");
        }
    }

    @Nested
    @DisplayName("경계/실패 케이스")
    class EdgeAndFailCases {
        @Test
        @DisplayName("해당 region에 RealmEntity가 없으면 빈 리스트를 반환한다")
        void findByRegion_returnsEmptyWhenNone() {
            // given: 아무 데이터도 저장하지 않음
            // when
            List<Realm> result = realmReadRepository.findByRegion(RegionType.KR);
            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 region에 동일 slug/connectedRealmId가 있어도 region별로 구분된다")
        void sameSlugOrConnectedRealmIdDifferentRegion() {
            RealmEntity krRealm = createEntity(1L, "azshara", 1001L, RegionType.KR, Map.of("ko_KR", "아즈샤라"));
            RealmEntity usRealm = createEntity(2L, "azshara", 1001L, RegionType.US, Map.of("en_US", "Azshara"));
            realmJpaRepository.saveAll(List.of(krRealm, usRealm));
            List<Realm> krResult = realmReadRepository.findByRegion(RegionType.KR);
            List<Realm> usResult = realmReadRepository.findByRegion(RegionType.US);
            assertThat(krResult).hasSize(1);
            assertThat(krResult.get(0).getRegion()).isEqualTo(RegionType.KR);
            assertThat(usResult).hasSize(1);
            assertThat(usResult.get(0).getRegion()).isEqualTo(RegionType.US);
        }
    }
}
