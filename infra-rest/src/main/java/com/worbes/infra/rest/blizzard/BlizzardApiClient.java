package com.worbes.infra.rest.blizzard;

import java.util.Map;

public interface BlizzardApiClient {
    <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType);
}
