package com.worbes.infra.rest.core.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@Slf4j
public class RestClientAdapter implements RestApiClient {

    private final RestClient restClient;
    private final RestApiErrorHandler errorHandler;

    public RestClientAdapter(RestClient.Builder builder, RestApiErrorHandler errorHandler) {
        this.restClient = builder.build();
        this.errorHandler = errorHandler;
    }

    @Override
    public <T> T get(RequestParams params, Class<T> responseType) {
        URI uri = buildUri(params.url(), params.queryParams());

        return restClient.get()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .headers(headers -> params.headers().forEach(headers::add))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    @Override
    public <T> T post(RequestParams params, Class<T> responseType) {
        URI uri = buildUri(params.url(), params.queryParams());

        return restClient.post()
                .uri(uri)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .headers(headers -> params.headers().forEach(headers::add))
                .body(params.body())
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    private URI buildUri(String url, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        queryParams.forEach(builder::queryParam);
        return builder.build().toUri();
    }
}
