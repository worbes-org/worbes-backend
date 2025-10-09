package com.worbes.application.common.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalizedName {

    private final EnumMap<LocaleCode, String> values;

    private LocalizedName(Map<LocaleCode, String> values) {
        if (values == null || values.isEmpty()) {
            this.values = new EnumMap<>(LocaleCode.class);
        } else {
            this.values = new EnumMap<>(values);
        }
    }

    public static LocalizedName fromLocalized(Map<LocaleCode, String> values) {
        return new LocalizedName(values);
    }

    public static LocalizedName fromRaw(Map<String, String> raw) {
        if (raw == null || raw.isEmpty()) {
            return new LocalizedName(null);
        }
        EnumMap<LocaleCode, String> parsed = new EnumMap<>(LocaleCode.class);
        for (Map.Entry<String, String> entry : raw.entrySet()) {
            parsed.put(LocaleCode.fromValue(entry.getKey()), entry.getValue());
        }

        return new LocalizedName(parsed);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Map<String, String> asRaw() {
        if (isEmpty()) return null;
        return values.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getValue(),
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
