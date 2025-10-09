package com.worbes.api;

import com.worbes.adapter.web.auction.model.GetAuctionDetailResponse;
import com.worbes.adapter.web.auction.model.GetAuctionTrendResponse;
import com.worbes.adapter.web.common.ApiResponse;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(scripts = "/sql/get_auction_detail_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class GetAuctionDetailControllerSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auctions";
    }

    @Test
    @DisplayName("아이템 상세 정보 조회")
    void getAuctionDetail_returnsDetailAndTrend() {
        Long itemId = 1001L;
        String url = String.format("%s/%d?region=KR&realmId=1", baseUrl(), itemId);

        ResponseEntity<ApiResponse<GetAuctionDetailResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<GetAuctionDetailResponse> body = response.getBody();
        assertThat(body).isNotNull();

        GetAuctionDetailResponse detail = body.content();

        // 기본 정보 검증
        assertThat(detail.lowestPrice()).isEqualTo(2_000_000L);
        assertThat(detail.totalQuantity()).isEqualTo(2);

        // quantityByPrice 검증
        Map<Long, Integer> quantityByPrice = detail.quantityByPrice();
        assertThat(quantityByPrice).isNotEmpty();
        assertThat(quantityByPrice).containsEntry(2_000_000L, 1);
        assertThat(quantityByPrice).containsEntry(2_000_500L, 1);

        // stats 검증
        GetAuctionTrendResponse stats = detail.historyResponse();
        assertThat(stats).isNotNull();
        assertThat(stats.averageLowestPrice()).isEqualTo(2_500_000L);
        assertThat(stats.medianLowestPrice()).isEqualTo(2_500_000L);

        // trends 검증
        assertThat(stats.trends()).hasSize(2);
    }

    @Test
    @DisplayName("itemBonus 파라미터 포함 조회")
    void getAuctionDetail_withItemBonus_returnsFilteredResults() {
        Long itemId = 1001L;
        String itemBonus = "9001:9003";
        String url = String.format("%s/%d?region=KR&realmId=1&itemBonus=%s", baseUrl(), itemId, itemBonus);

        ResponseEntity<ApiResponse<GetAuctionDetailResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GetAuctionDetailResponse detail = response.getBody().content();
        assertThat(detail.lowestPrice()).isEqualTo(2_000_000L);
        assertThat(detail.totalQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("itemBonus 형식 잘못되면 예외")
    void getAuctionDetail_invalidItemBonus_throwsException() {
        Long itemId = 1001L;
        String itemBonus = "9001:abc"; // 잘못된 형식
        String url = String.format("%s/%d?region=KR&realmId=1&itemBonus=%s", baseUrl(), itemId, itemBonus);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).contains("itemBonus 형식이 잘못되었습니다.");
    }

    @Test
    @DisplayName("아이템 1002의 경매 상세 및 트렌드 검증")
    void getAuctionDetail_item1002_correctTrends() {
        String url = String.format("http://localhost:%s/api/v1/auctions/1002?region=KR&realmId=1", port);

        ResponseEntity<ApiResponse<GetAuctionDetailResponse>> response = restTemplate.exchange(
                url,  // realmId=null이므로 URL 그대로
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        GetAuctionDetailResponse detail = response.getBody().content();

        // 경매 요약 검증
        assertThat(detail.lowestPrice()).isEqualTo(5000L);
        assertThat(detail.totalQuantity()).isEqualTo(350 + 150); // 2개의 최근 snapshot 합계
        assertThat(detail.quantityByPrice()).containsEntry(5000L, 350);
        assertThat(detail.quantityByPrice()).containsEntry(6000L, 150);

        // 트렌드 검증
        GetAuctionTrendResponse stats = detail.historyResponse();
        assertThat(stats.trends()).hasSize(2); // now, now-1h
        assertThat(stats.averageLowestPrice()).isEqualTo((5000 + 6000) / 2);
        assertThat(stats.medianLowestPrice()).isEqualTo(5500L);
    }
}
