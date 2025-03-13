package com.worbes.auctionhousetracker.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocaleType {
    EN_US("en_US"),
    KO_KR("ko_KR"),
    FR_FR("fr_FR"),
    DE_DE("de_DE"),
    ZH_CN("zh_CN"),
    ZH_TW("zh_TW"),
    ES_ES("es_ES"),
    ES_MX("es_MX"),
    IT_IT("it_IT"),
    RU_RU("ru_RU"),
    PT_BR("pt_BR");

    private final String code;
}
