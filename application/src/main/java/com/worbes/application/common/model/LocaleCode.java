package com.worbes.application.common.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum LocaleCode {
    DE_DE("de_DE"),
    EN_GB("en_GB"),
    EN_US("en_US"),
    ES_ES("es_ES"),
    ES_MX("es_MX"),
    FR_FR("fr_FR"),
    IT_IT("it_IT"),
    KO_KR("ko_KR"),
    PT_BR("pt_BR"),
    RU_RU("ru_RU"),
    ZH_CN("zh_CN"),
    ZH_TW("zh_TW");

    private static final Map<String, LocaleCode> VALUE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(LocaleCode::getValue, Function.identity()));
    private final String value;


    LocaleCode(String value) {
        this.value = value;
    }

    public static LocaleCode fromValue(String value) {
        LocaleCode code = VALUE_MAP.get(value);
        if (code == null) {
            throw new IllegalArgumentException("Unknown locale code: " + value);
        }
        return code;
    }
}
