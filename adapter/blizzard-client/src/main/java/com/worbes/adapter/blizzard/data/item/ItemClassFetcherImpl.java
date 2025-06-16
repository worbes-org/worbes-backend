package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.application.item.port.out.ItemClassFetcher;
import com.worbes.application.item.port.out.ItemClassIndexFetchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemClassFetcherImpl implements ItemClassFetcher {

    private final BlizzardApiClient apiClient;
    private final BlizzardApiUriFactory uriFactory;
    private final ItemClassIndexResponseMapper itemClassIndexDtoMapper;

    public List<ItemClassIndexFetchResult> fetchItemClassIndex() {
        URI uri = uriFactory.itemClassesIndexUri();
        ItemClassesIndexResponse response = apiClient.fetch(uri, ItemClassesIndexResponse.class);

        return itemClassIndexDtoMapper.toDtoList(response.getItemClasses());
    }
}
