package com.worbes.application.realm.model;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum RegionType implements Serializable {

    US("us"),
    KR("kr");

    private final String value;

    RegionType(String value) {
        this.value = value;
    }

}
