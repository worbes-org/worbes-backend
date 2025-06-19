package com.worbes.web;

public record SearchAuctionRequest(
        String region,
        Long realmId,
        String locale,
        Long itemClassId,
        String itemName,
        Long itemSubclassId
) {
}
