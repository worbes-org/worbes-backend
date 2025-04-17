package com.worbes.adapter.blizzard.client;

import java.util.Map;

public interface BlizzardApiClient {
    <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType);
}
