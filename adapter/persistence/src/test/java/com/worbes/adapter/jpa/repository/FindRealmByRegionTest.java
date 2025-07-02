package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.RealmEntity;
import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.FindRealmRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Integration::FindRealmRepository::findByRegion")
public class FindRealmByRegionTest {

    @Autowired
    private FindRealmRepository findRealmRepository;

    @Autowired
    private RealmJpaRepository realmJpaRepository;

    @Test
    @DisplayName("region에 해당하는 RealmEntity들을 찾아 Realm 도메인으로 반환한다")
    void findByRegion_통합테스트() {
        // given
        RegionType kr = RegionType.KR;
        RegionType us = RegionType.US;
        RealmEntity realm1 = RealmEntity.builder()
                .id(1L)
                .slug("azshara")
                .connectedRealmId(1001L)
                .name(Map.of("ko_KR", "아즈샤라", "en_US", "Azshara"))
                .region(kr)
                .build();

        RealmEntity realm2 = RealmEntity.builder()
                .id(2L)
                .slug("stormrage")
                .connectedRealmId(1002L)
                .name(Map.of("ko_KR", "스톰레이지", "en_US", "Stormrage"))
                .region(kr)
                .build();

        RealmEntity realm3 = RealmEntity.builder()
                .id(3L)
                .slug("rexxar")
                .connectedRealmId(1003L)
                .name(Map.of("ko_KR", "렉사르", "en_US", "Rexxar"))
                .region(us)
                .build();

        realmJpaRepository.saveAll(List.of(realm1, realm2, realm3));

        // when
        List<Realm> result1 = findRealmRepository.findByRegion(kr);
        List<Realm> result2 = findRealmRepository.findByRegion(us);

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
