package com.worbes.application.common.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalizedNameTest {

    @Test
    @DisplayName("LocaleCode.fromValue()는 문자열을 Enum으로 변환한다")
    void fromValue_success() {
        assertThat(LocaleCode.fromValue("ko_KR")).isEqualTo(LocaleCode.KO_KR);
        assertThat(LocaleCode.fromValue("en_US")).isEqualTo(LocaleCode.EN_US);
    }

    @Test
    @DisplayName("LocaleCode.fromValue()는 존재하지 않는 코드면 예외 발생")
    void fromValue_invalid() {
        assertThatThrownBy(() -> LocaleCode.fromValue("jp_JP"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown locale code");
    }

    @Test
    @DisplayName("fromRaw()는 문자열 기반 맵을 EnumMap으로 변환한다")
    void fromRaw() {
        Map<String, String> raw = new HashMap<>();
        raw.put("ko_KR", "한국어");
        raw.put("en_US", "English");

        LocalizedName localizedName = LocalizedName.fromRaw(raw);

        assertThat(localizedName.get(LocaleCode.KO_KR)).isEqualTo("한국어");
        assertThat(localizedName.get(LocaleCode.EN_US)).isEqualTo("English");
    }

    @Test
    @DisplayName("asRaw()는 EnumMap을 문자열 기반 맵으로 되돌린다")
    void asRaw() {
        Map<String, String> raw = new HashMap<>();
        raw.put("ko_KR", "한국어");
        raw.put("en_US", "English");

        LocalizedName localizedName = LocalizedName.fromRaw(raw);
        Map<String, String> result = localizedName.asRaw();

        assertThat(result).containsEntry("ko_KR", "한국어");
        assertThat(result).containsEntry("en_US", "English");
    }

    @Test
    @DisplayName("getPreferred()는 우선순위대로 Locale 값을 반환한다")
    void getPreferred() {
        Map<String, String> raw = Map.of(
                "en_US", "English",
                "ko_KR", "한국어"
        );
        LocalizedName name = LocalizedName.fromRaw(raw);

        List<LocaleCode> priority = List.of(LocaleCode.ZH_CN, LocaleCode.KO_KR, LocaleCode.EN_US);

        assertThat(name.getPreferred(priority)).isEqualTo("한국어");
    }
}
