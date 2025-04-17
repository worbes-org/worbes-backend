package com.worbes.client.wow;

import java.util.Map;

public interface BlizzardApiClient {
    <T> T fetch(String url, Map<String, String> queryParams, Class<T> responseType);
}
