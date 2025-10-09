package com.worbes.api;

import com.worbes.adapter.web.common.ApiResponse;
import com.worbes.adapter.web.realm.model.GetRealmResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(scripts = "/sql/realm_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class GetRealmControllerSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/realms";
    }

    @Test
    @DisplayName("KR 지역 Realm 조회")
    void getRealms_KR_returnsAllKRRealms() {
        String url = baseUrl() + "?region=KR";

        ResponseEntity<ApiResponse<List<GetRealmResponse>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<List<GetRealmResponse>> body = response.getBody();
        assertThat(body).isNotNull();
        List<GetRealmResponse> realms = body.content();

        assertThat(realms).isNotEmpty();
        assertThat(realms.size()).isEqualTo(15);

        assertThat(realms).anySatisfy(r -> {
            assertThat(r.id()).isEqualTo(205L);
            assertThat(r.connectedRealmId()).isEqualTo(205L);
            assertThat(r.name()).containsEntry("ko_KR", "아즈샤라");
        });

        assertThat(realms).anySatisfy(r -> {
            assertThat(r.id()).isEqualTo(258L);
            assertThat(r.connectedRealmId()).isEqualTo(214L);
            assertThat(r.name()).containsEntry("ko_KR", "알렉스트라자");
        });
    }

    @Test
    @DisplayName("존재하지 않는 지역 조회 시 빈 결과")
    void getRealms_NonExistRegion_returnsEmptyList() {
        String url = baseUrl() + "?region=US";

        ResponseEntity<ApiResponse<List<GetRealmResponse>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<List<GetRealmResponse>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.content()).isEmpty();
    }
}
