package com.worbes.auctionhousetracker.entity.enums;

public enum NamespaceType {
    DYNAMIC("dynamic-%s"),
    STATIC("static-%s");

    private final String format;

    NamespaceType(String format) {
        this.format = format;
    }

    public String format(Region region) {
        return String.format(this.format, region.getValue());
    }
}
