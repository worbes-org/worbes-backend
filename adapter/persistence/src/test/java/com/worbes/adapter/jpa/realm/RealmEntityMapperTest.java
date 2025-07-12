package com.worbes.adapter.jpa.realm;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

class RealmEntityMapperTest {

    private final RealmEntityMapper mapper = Mappers.getMapper(RealmEntityMapper.class);

    @Test
    @DisplayName("RealmEntity → Realm 매핑에 성공한다")
    void entityToDomain() {
        // Given
        RealmEntity entity = RealmEntity.builder()
                .id(1L)
                .connectedRealmId(100L)
                .region(RegionType.KR)
                .name(Map.of(LocaleCode.KO_KR.getValue(), "하이잘", LocaleCode.EN_US.getValue(), "Hyjal"))
                .slug("hyjal")
                .build();

        // When
        Realm result = mapper.toDomain(entity);

        // Then
        then(result.getId()).isEqualTo(entity.getId());
        then(result.getConnectedRealmId()).isEqualTo(entity.getConnectedRealmId());
        then(result.getRegion()).isEqualTo(entity.getRegion());
        then(result.getName()).isEqualTo(entity.getName());
        then(result.getSlug()).isEqualTo(entity.getSlug());
    }

    @Test
    @DisplayName("Realm → RealmEntity 매핑에 성공한다")
    void domainToEntity() {
        // Given
        Realm domain = Realm.builder()
                .id(2L)
                .connectedRealmId(200L)
                .region(RegionType.KR)
                .name(Map.of(LocaleCode.EN_US.getValue(), "Stormrage", LocaleCode.KO_KR.getValue(), "스톰레이지"))
                .slug("stormrage")
                .build();

        // When
        RealmEntity result = mapper.toEntity(domain);

        // Then
        then(result.getId()).isEqualTo(domain.getId());
        then(result.getConnectedRealmId()).isEqualTo(domain.getConnectedRealmId());
        then(result.getRegion()).isEqualTo(domain.getRegion());
        then(result.getName()).isEqualTo(domain.getName());
        then(result.getSlug()).isEqualTo(domain.getSlug());
    }
}
