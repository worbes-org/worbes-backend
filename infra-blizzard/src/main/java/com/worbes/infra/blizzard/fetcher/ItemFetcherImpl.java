package com.worbes.infra.blizzard.fetcher;

import com.worbes.application.core.item.dto.ItemClassDto;
import com.worbes.application.core.item.port.ItemFetcher;
import com.worbes.domain.shared.RegionType;
import com.worbes.infra.blizzard.client.BlizzardApiParamsBuilder;
import com.worbes.infra.blizzard.client.BlizzardApiUrlBuilder;
import com.worbes.infra.blizzard.mapper.ItemClassDtoMapper;
import com.worbes.infra.blizzard.response.ItemClassesIndexResponse;
import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.GetRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.worbes.infra.blizzard.enums.NamespaceType.STATIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemFetcherImpl implements ItemFetcher {

    private static final RegionType REGION = RegionType.KR;
    private final RestApiClient restApiClient;
    private final ItemClassDtoMapper mapper;

    public List<ItemClassDto> fetchItemClasses() {
        String path = BlizzardApiUrlBuilder.builder(REGION).itemClassIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(REGION).namespace(STATIC).build();
        GetRequestBuilder request = GetRequestBuilder.builder()
                .url(path)
                .queryParams(params)
                .build();
        ItemClassesIndexResponse response = restApiClient.get(request, ItemClassesIndexResponse.class);
        return mapper.toDtoList(response.getItemClassIndexElements());
    }
}
