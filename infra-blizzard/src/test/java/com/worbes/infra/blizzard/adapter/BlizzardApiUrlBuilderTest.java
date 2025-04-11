package com.worbes.infra.blizzard.adapter;

import com.worbes.domain.shared.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlizzardApiUrlBuilderTest {

    /**
     * 필수 파라미터 누락 (region)
     * path 설정 누락
     * 모든 API 메서드별 URL 조립 확인
     */

    @Test
    @DisplayName("region이 null이면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenRegionIsNull() {
        assertThatThrownBy(() -> BlizzardApiUrlBuilder.builder(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Region은 필수값");
    }

    @Test
    @DisplayName("경로 설정 없이 build() 호출 시 예외 발생")
    void shouldThrowExceptionIfPathNotSet() {
        BlizzardApiUrlBuilder builder = BlizzardApiUrlBuilder.builder(RegionType.KR);

        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("경로(path)");
    }

    @Nested
    @DisplayName("각 API 메서드는 올바른 경로로 URL을 생성해야 한다")
    class UrlBuildTest {

        @Test
        void shouldBuildItemClassIndexUrl() {
            String url = BlizzardApiUrlBuilder.builder(RegionType.KR)
                    .itemClassIndex()
                    .build();

            assertThat(url).isEqualTo("https://kr.api.blizzard.com/data/wow/item-class/index");
        }

        @Test
        void shouldBuildItemClassUrl() {
            String url = BlizzardApiUrlBuilder.builder(RegionType.KR)
                    .itemClass(5L)
                    .build();

            assertThat(url).isEqualTo("https://kr.api.blizzard.com/data/wow/item-class/5");
        }

        @Test
        void shouldBuildItemSubclassUrl() {
            String url = BlizzardApiUrlBuilder.builder(RegionType.KR)
                    .itemSubclass(2L, 7L)
                    .build();

            assertThat(url).isEqualTo("https://kr.api.blizzard.com/data/wow/item-class/2/item-subclass/7");
        }

        @Test
        void shouldBuildCommoditiesUrl() {
            String url = BlizzardApiUrlBuilder.builder(RegionType.KR)
                    .commodities()
                    .build();

            assertThat(url).isEqualTo("https://kr.api.blizzard.com/data/wow/auctions/commodities");
        }

        @Test
        void shouldBuildRealmUrl() {
            String url = BlizzardApiUrlBuilder.builder(RegionType.KR)
                    .realm("azshara")
                    .build();

            assertThat(url).isEqualTo("https://kr.api.blizzard.com/data/wow/realm/azshara");
        }
    }
}
