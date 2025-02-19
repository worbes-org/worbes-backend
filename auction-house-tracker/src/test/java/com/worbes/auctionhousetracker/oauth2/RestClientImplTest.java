package com.worbes.auctionhousetracker.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import com.worbes.auctionhousetracker.exception.InternalServerErrorException;
import com.worbes.auctionhousetracker.exception.RestApiClientException;
import com.worbes.auctionhousetracker.exception.TooManyRequestsException;
import com.worbes.auctionhousetracker.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Slf4j
@ActiveProfiles("test")
@RestClientTest(RestApiClient.class)
@EnableConfigurationProperties(RestClientConfigProperties.class)
class RestClientImplTest {

    @Autowired
    RestClientConfigProperties properties;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    RestApiClient restApiClient;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AccessTokenHandler tokenService;

    private String requestUri, expectedUri;
    private Map<String, String> requestParams;

    @BeforeEach
    void setUp() {
        this.requestUri = "https://kr.api.blizzard.com/data/wow/item-class/index";
        this.expectedUri = "https://kr.api.blizzard.com/data/wow/item-class/index?namespace=static-kr";
        this.requestParams = Map.of("namespace", "static-kr");
    }


    @DisplayName("정상적인 데이터 요청 시 응답 반환")
    @Test
    void fetchData_WhenSuccessful_ShouldReturnData() {
        // Given: 서버가 정상 응답을 반환
        String expectedResponse = "{\"data\":\"sampleData\"}";
        server.expect(ExpectedCount.once(), requestTo(expectedUri))
                .andRespond(MockRestResponseCreators.withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // When: fetchData 호출
        String result = restApiClient.get(requestUri, requestParams, String.class);

        // Then: 응답이 정상적으로 반환되어야 한다
        assertEquals(expectedResponse, result);

        // 서버가 예상대로 호출되었는지 확인
        server.verify();
    }

    @DisplayName("데이터 요청 시 TOO_MANY_REQUESTS 오류 발생 시 recover 메서드 호출")
    @Test
    void fetchData_WhenTooManyRequests_ShouldInvokeRecover() {
        // Given: 서버가 TOO_MANY_REQUESTS 상태 코드로 응답
        server.expect(ExpectedCount.manyTimes(), requestTo(expectedUri))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        // When: fetchData 호출
        assertThatThrownBy(() -> {
            restApiClient.get(requestUri, requestParams, String.class);
        }).isInstanceOf(TooManyRequestsException.class);

        server.verify(); // 서버가 예상대로 호출되었는지 확인
    }

    @DisplayName("서버가 인증 오류를 반환할 때 recover 메서드 호출")
    @Test
    void fetchData_WhenUnauthorized_ShouldInvokeRecover() {
        // Given: 서버가 UNAUTHORIZED 상태 코드로 응답
        server.expect(ExpectedCount.manyTimes(), requestTo(expectedUri))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // When: fetchData 호출
        assertThatThrownBy(() -> {
            restApiClient.get(requestUri, requestParams, String.class);
        }).isInstanceOf(UnauthorizedException.class);

        // Then: @Recover 메서드가 호출되므로 결과는 null이어야 한다
        server.verify();
        // AccessTokenService의 refresh 메소드가 호출되어야 함
        verify(tokenService, times(2)).refresh();
    }

    @DisplayName("서버가 INTERNAL_SERVER_ERROR을 반환할 때 recover 메서드 호출")
    @Test
    void fetchData_WhenInternalServerError_ShouldInvokeRecover() {
        // Given: 서버가 INTERNAL_SERVER_ERROR 상태 코드로 응답
        server.expect(ExpectedCount.manyTimes(), requestTo(expectedUri))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));


        // When: fetchData 호출
        assertThatThrownBy(() -> {
            restApiClient.get(requestUri, requestParams, String.class);
        }).isInstanceOf(InternalServerErrorException.class);

        server.verify();
    }

    @DisplayName("예상치 못한 오류 발생 시 recover 메서드 호출")
    @Test
    void fetchData_WhenUnexpectedError_ShouldInvokeRecover() {
        server.expect(ExpectedCount.manyTimes(), requestTo(expectedUri))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)); // 테스트 목적으로 사용

        // When: fetchData 호출
        assertThatThrownBy(() -> {
            restApiClient.get(requestUri, requestParams, String.class);
        }).isInstanceOf(RestApiClientException.class);

        server.verify();
    }

    @TestConfiguration
    @RequiredArgsConstructor
    @EnableConfigurationProperties(RestClientConfigProperties.class)
    static class TestConfig {

        private final RestClientConfigProperties properties;

        @Bean
        RestClient apiClient(RestClient.Builder builder) {
            return builder.baseUrl(properties.getBaseUrlKr()).build();
        }
    }
}
