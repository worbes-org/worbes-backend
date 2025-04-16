package com.worbes.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalizedNameTest {
    @Test
    @DisplayName("fromRaw()는 문자열 키 맵을 EnumMap으로 변환해 LocalizedName을 생성한다")
    void createFromRawMap() {
        Map<String, String> raw = Map.of(
                "ko_kr", "전투 애완동물",
                "en_us", "Battle Pets"
        );

        LocalizedName name = LocalizedName.fromRaw(raw);

        assertThat(name.get(LocaleCode.KO_KR)).isEqualTo("전투 애완동물");
        assertThat(name.get(LocaleCode.EN_US)).isEqualTo("Battle Pets");
    }

    @Test
    @DisplayName("fromLocalized()는 EnumMap을 그대로 사용할 수 있다")
    void createFromEnumMap() {
        EnumMap<LocaleCode, String> data = new EnumMap<>(LocaleCode.class);
        data.put(LocaleCode.KO_KR, "전투 애완동물");

        LocalizedName name = LocalizedName.fromLocalized(data);

        assertThat(name.getKorean()).isEqualTo("전투 애완동물");
    }

    @Test
    @DisplayName("getPreferred()는 우선순위에 따라 적절한 이름을 반환한다")
    void preferredLocaleResolution() {
        Map<String, String> raw = Map.of(
                "en_us", "Battle Pets",
                "ko_kr", "전투 애완동물"
        );

        LocalizedName name = LocalizedName.fromRaw(raw);

        String result = name.getPreferred(List.of(
                LocaleCode.FR_FR, LocaleCode.KO_KR, LocaleCode.EN_US
        ));

        assertThat(result).isEqualTo("전투 애완동물");
    }

    @Test
    @DisplayName("fromRaw()는 유효하지 않은 로케일 키를 무시한다")
    void ignoreUnknownKeys() {
        Map<String, String> raw = Map.of(
                "unknown", "??",
                "ko_kr", "전투 애완동물"
        );

        LocalizedName name = LocalizedName.fromRaw(raw);

        assertThat(name.get(LocaleCode.KO_KR)).isEqualTo("전투 애완동물");
        assertThat(name.get(LocaleCode.EN_US)).isNull();
    }

    @Test
    @DisplayName("null 또는 빈 맵으로 생성하면 예외가 발생한다")
    void nullAndEmptyCheck() {
        assertThatThrownBy(() -> LocalizedName.fromRaw(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> LocalizedName.fromRaw(Map.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("LocalizedName.asRaw()는 EnumMap을 문자열 키 맵으로 변환한다")
    void testAsRawConversion() {
        LocalizedName name = LocalizedName.fromLocalized(Map.of(
                LocaleCode.KO_KR, "한글이름",
                LocaleCode.EN_US, "영문이름"
        ));

        Map<String, String> raw = name.asRaw();

        assertThat(raw).containsEntry("ko_kr", "한글이름");
        assertThat(raw).containsEntry("en_us", "영문이름");
    }
}
