package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemFetchResult;
import com.worbes.application.item.port.out.MediaFetchResult;
import org.springframework.stereotype.Component;

@Component
public class ItemFactory {

    public Item create(ItemFetchResult itemFetchResult, MediaFetchResult mediaFetchResult) {
        return Item.builder()
                .id(itemFetchResult.id())
                .itemClassId(itemFetchResult.itemClassId())
                .itemSubclassId(itemFetchResult.itemSubclassId())
                .level(itemFetchResult.level())
                .name(itemFetchResult.name())
                .inventoryType(itemFetchResult.inventoryType())
                .quality(itemFetchResult.quality())
                .previewItem(itemFetchResult.previewItem())
                .iconUrl(mediaFetchResult.iconUrl())
                .build();
    }
}
