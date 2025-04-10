package com.worbes.application.core.item.port;

import com.worbes.application.core.item.dto.ItemClassDto;

import java.util.List;

public interface ItemFetcher {
    List<ItemClassDto> fetchItemClasses();
}
