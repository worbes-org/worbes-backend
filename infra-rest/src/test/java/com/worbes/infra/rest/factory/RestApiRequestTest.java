package com.worbes.infra.rest.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RestApiRequestTest {

    @Test
    @DisplayName("null 값이 전달되면 기본값으로 대체되어야 한다")
    void shouldSetDefaultsWhenNullsPassed() {
        RestApiRequest request = RestApiRequest.builder()
                .url("https://example.com/api")
                .build();

        assertThat(request.url()).isEqualTo("https://example.com/api");
        assertThat(request.queryParams()).isEmpty();
        assertThat(request.headers()).isEmpty();
        assertThat(request.body()).isEqualTo("");
    }

    @Test
    @DisplayName("모든 필드를 명시적으로 전달하면 그대로 세팅된다")
    void shouldCreateRequestWithAllFields() {
        Map<String, String> query = Map.of("q", "test");
        Map<String, String> headers = Map.of("Authorization", "Bearer xyz");
        Object body = Map.of("key", "value");

        RestApiRequest request = RestApiRequest.builder()
                .url("https://api.example.com/resource")
                .queryParams(query)
                .headers(headers)
                .body(body)
                .build();

        assertThat(request.url()).isEqualTo("https://api.example.com/resource");
        assertThat(request.queryParams()).isEqualTo(query);
        assertThat(request.headers()).isEqualTo(headers);
        assertThat(request.body()).isEqualTo(body);
    }

    @Test
    @DisplayName("빈 값도 정상적으로 처리되어야 한다")
    void shouldHandleEmptyMaps() {
        RestApiRequest request = RestApiRequest.builder()
                .url("https://example.com")
                .queryParams(Collections.emptyMap())
                .headers(Collections.emptyMap())
                .body("")
                .build();

        assertThat(request.queryParams()).isEmpty();
        assertThat(request.headers()).isEmpty();
        assertThat(request.body()).isEqualTo("");
    }
}
