package com.worbes.infra.rest.blizzard.fetcher;

import com.worbes.domain.shared.RegionType;
import com.worbes.infra.rest.blizzard.client.BlizzardApiParamsBuilder;
import com.worbes.infra.rest.blizzard.client.BlizzardApiUrlBuilder;
import com.worbes.infra.rest.blizzard.response.ItemClassesIndexResponse;
import com.worbes.infra.rest.common.client.RestApiClient;
import com.worbes.infra.rest.common.factory.GetRequestBuilder;
import com.worbes.infra.rest.common.oauth.AccessTokenHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.worbes.infra.rest.blizzard.enums.NamespaceType.STATIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemFetcherImpl {

    private static final RegionType KR = RegionType.KR;
    private final RestApiClient restApiClient;
    private final AccessTokenHandler accessTokenHandler;

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
