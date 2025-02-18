package com.worbes.auctionhousetracker.oauth2;

import java.util.Map;

public interface RestApiClient {

    <T> T get(String path, Map<String, String> params, Class<T> responseType);

}
