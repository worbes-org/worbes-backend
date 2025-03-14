package com.worbes.auctionhousetracker.entity.embeded;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class Translation {
    private String en_US;
    private String es_MX;
    private String pt_BR;
    private String de_DE;
    private String en_GB;
    private String es_ES;
    private String fr_FR;
    private String it_IT;
    private String ru_RU;
    private String ko_KR;
    private String zh_TW;
    private String zh_CN;
}
