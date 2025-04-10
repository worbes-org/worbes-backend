package com.worbes.infra.blizzard.factory;

import com.worbes.domain.shared.RegionType;
import com.worbes.infra.blizzard.enums.NamespaceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlizzardApiParamsBuilderTest {

    /**
     * 필수 파라미터 누락 방지 (region, namespace)
     * 올바른 namespace 포맷 확인
     * 빌드 결과 불변성 보장
     */

    @Test
    @DisplayName("builder() 호출 시 region이 null이면 예외를 던진다")
    void shouldThrowExceptionWhenRegionIsNull() {
        assertThatThrownBy(() -> BlizzardApiParamsBuilder.builder(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Region은 필수값");
    }

    @Test
    @DisplayName("namespace() 없이 build() 호출 시 예외 발생")
    void shouldThrowExceptionWhenNamespaceIsNotSet() {
        BlizzardApiParamsBuilder builder = BlizzardApiParamsBuilder.builder(RegionType.KR);

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("namespace");
    }

    @Test
    @DisplayName("namespace() 호출 시 region에 따라 올바른 namespace 포맷을 설정한다")
    void shouldSetNamespaceCorrectly() {
        Map<String, String> params = BlizzardApiParamsBuilder.builder(RegionType.KR)
                .namespace(NamespaceType.STATIC)
                .build();

        assertThat(params).containsEntry("namespace", "static-kr");
    }

    @Test
    @DisplayName("build()는 새로운 Map을 반환한다 (불변성 테스트)")
    void buildShouldReturnNewMap() {
        BlizzardApiParamsBuilder builder = BlizzardApiParamsBuilder.builder(RegionType.KR)
                .namespace(NamespaceType.STATIC);

        Map<String, String> first = builder.build();
        Map<String, String> second = builder.build();

        first.put("namespace", "hack");

        assertThat(second.get("namespace")).isEqualTo("static-kr");
    }
}
