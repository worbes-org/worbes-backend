package com.worbes.domain.shared;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalizedName {

    private final EnumMap<LocaleCode, String> values;

    private LocalizedName(Map<LocaleCode, String> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("이름은 하나 이상 필요합니다.");
        }
        this.values = new EnumMap<>(values);
    }

    public static LocalizedName fromLocalized(Map<LocaleCode, String> values) {
        return new LocalizedName(values);
    }

    public static LocalizedName fromRaw(Map<String, String> raw) {
        if (raw == null || raw.isEmpty()) {
            throw new IllegalArgumentException("이름은 하나 이상 필요합니다.");
        }

        EnumMap<LocaleCode, String> parsed = new EnumMap<>(LocaleCode.class);

        for (Map.Entry<String, String> entry : raw.entrySet()) {
            safeParse(entry.getKey()).ifPresent(locale ->
                    parsed.put(locale, entry.getValue())
            );
        }

        return new LocalizedName(parsed);
    }

    private static Optional<LocaleCode> safeParse(String rawKey) {
        return switch (rawKey.toLowerCase()) {
            case "ko_kr" -> Optional.of(LocaleCode.KO_KR);
            case "en_us" -> Optional.of(LocaleCode.EN_US);
            case "en_gb" -> Optional.of(LocaleCode.EN_GB);
            case "de_de" -> Optional.of(LocaleCode.DE_DE);
            case "fr_fr" -> Optional.of(LocaleCode.FR_FR);
            case "es_es" -> Optional.of(LocaleCode.ES_ES);
            case "es_mx" -> Optional.of(LocaleCode.ES_MX);
            case "it_it" -> Optional.of(LocaleCode.IT_IT);
            case "pt_br" -> Optional.of(LocaleCode.PT_BR);
            case "ru_ru" -> Optional.of(LocaleCode.RU_RU);
            case "zh_cn" -> Optional.of(LocaleCode.ZH_CN);
            case "zh_tw" -> Optional.of(LocaleCode.ZH_TW);
            default -> Optional.empty();
        };
    }

    public Map<String, String> asRaw() {
        return values.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name().toLowerCase(), // KO_KR → "ko_kr"
                        Map.Entry::getValue
                ));
    }

    public String get(LocaleCode locale) {
        return values.get(locale);
    }

    public String getPreferred(List<LocaleCode> priorities) {
        for (LocaleCode locale : priorities) {
            if (values.containsKey(locale)) return values.get(locale);
        }
        return values.values().iterator().next();
    }

    public String getKorean() {
        return get(LocaleCode.KO_KR);
    }

    @Override
    public String toString() {
        return getKorean();
    }
}
