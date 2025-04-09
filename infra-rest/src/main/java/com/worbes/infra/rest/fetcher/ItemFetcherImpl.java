package com.worbes.infra.rest.fetcher;

import com.worbes.domain.shared.RegionType;
import com.worbes.infra.rest.client.RestApiClient;
import com.worbes.infra.rest.factory.BlizzardApiParamsBuilder;
import com.worbes.infra.rest.factory.BlizzardApiUrlBuilder;
import com.worbes.infra.rest.factory.GetRequestBuilder;
import com.worbes.infra.rest.response.ItemClassesIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.worbes.infra.rest.enums.NamespaceType.STATIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemFetcherImpl {

    private static final RegionType KR = RegionType.KR;
    private final RestApiClient restApiClient;

    public ItemClassesIndexResponse fetchItemClassesIndex() {
        String path = BlizzardApiUrlBuilder.builder(KR).itemClassIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(KR).namespace(STATIC).build();
        GetRequestBuilder request = GetRequestBuilder.builder()
                .url(path)
                .queryParams(params)
                .build();
        return restApiClient.get(request, ItemClassesIndexResponse.class);
    }
}
