package com.worbes.infra.rest.client;

import com.worbes.infra.rest.factory.RestApiRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;


@Slf4j
@Component
public class RestApiClientImpl implements RestApiClient {

    private final RestClient restClient;
    private final RestApiErrorHandler errorHandler;

    public RestApiClientImpl(RestClient.Builder builder, RestApiErrorHandler errorHandler) {
        this.restClient = builder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.errorHandler = errorHandler;
    }

    @Override
    public <T> T get(RestApiRequest request, Class<T> responseType) {
        URI uri = buildUri(request.url(), request.queryParams());

        return restClient.get()
                .uri(uri)
                .headers(headers -> request.headers().forEach(headers::add))
                .retrieve()
                .onStatus(HttpStatusCode::isError, errorHandler::handle)
                .body(responseType);
    }

    @Override
    public <T> T post(RestApiRequest request, Class<T> responseType) {
        URI uri = buildUri(request.url(), request.queryParams());

        return restClient.post()
                .uri(uri)
                .headers(headers -> request.headers().forEach(headers::add))
                .body(request.body())
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
