package com.worbes.adapter.blizzard.data.realm;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.RealmFetchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit::RealmResponseMapper")
class RealmFetchResultMapperTest {

    private final RealmResponseMapper mapper = Mappers.getMapper(RealmResponseMapper.class);

    @Test
    @DisplayName("RealmResponse를 RealmFetchResult로 올바르게 매핑한다")
    void shouldMapRealmResponseToDto() {
        // given
        RealmResponse response = new RealmResponse();
        response.setId(100L);
        response.setName(Map.of("ko-kr", "아즈샤라"));
        response.setConnectedRealmHref("https://api.test/connected-realm/1");
        response.setSlug("test-realm");

        RegionType region = RegionType.KR;

        // when
        RealmFetchResult dto = mapper.toDto(response, region);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(response.getId());
        assertThat(dto.name()).isEqualTo(response.getName());
        assertThat(dto.connectedRealmHref()).isEqualTo(response.getConnectedRealmHref());
        assertThat(dto.slug()).isEqualTo(response.getSlug());
        assertThat(dto.region()).isEqualTo(region);
    }
}
