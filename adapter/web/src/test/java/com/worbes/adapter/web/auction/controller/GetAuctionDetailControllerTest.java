package com.worbes.adapter.web.auction.controller;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetAuctionDetailResult;
import com.worbes.application.auction.port.in.GetAuctionDetailUseCase;
import com.worbes.application.auction.port.in.GetAuctionTrendResult;
import com.worbes.application.auction.port.in.GetAuctionTrendUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GetAuctionDetailController.class)
class GetAuctionDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAuctionDetailUseCase getAuctionDetailUseCase;

    @MockBean
    private GetAuctionTrendUseCase getAuctionTrendUseCase;

    @BeforeEach
    void setup() {
        // 기본 Mock 동작 설정
        // GetAuctionDetailUseCase Mock
        Mockito.when(getAuctionDetailUseCase.execute(any()))
                .thenReturn(new GetAuctionDetailResult(
                        100L,
                        5,
                        Map.of(100L, 2, 120L, 3)
                ));

        // GetAuctionTrendUseCase Mock
        List<AuctionTrendPoint> trendPoints = List.of(
                new AuctionTrendPoint(1L, 12345L, List.of(100L, 101L), Instant.parse("2025-08-20T00:00:00Z"), 90L, 2),
                new AuctionTrendPoint(2L, 12345L, List.of(100L, 101L), Instant.parse("2025-08-21T00:00:00Z"), 95L, 3)
        );

        Mockito.when(getAuctionTrendUseCase.execute(any()))
                .thenReturn(new GetAuctionTrendResult(
                        92L,      // averageLowestPrice
                        93L,      // medianLowestPrice
                        trendPoints
                ));
    }

    @Test
    @DisplayName("정상 요청 시 경매 상세 정보를 반환한다")
    void returnsAuctionDetailSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/auctions/12345")
                        .param("region", "KR")
                        .param("realmId", "1")
                        .param("itemBonus", "100:101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // 최저가, 총 수량, 현재 경매 확인
                .andExpect(jsonPath("$.content.lowest_price").value(100))
                .andExpect(jsonPath("$.content.total_quantity").value(5))
                .andExpect(jsonPath("$.content.current_auctions.100").value(2))
                .andExpect(jsonPath("$.content.current_auctions.120").value(3))
                // 트렌드 평균, 중앙값 확인
                .andExpect(jsonPath("$.content.stats.average_lowest_price").value(92))
                .andExpect(jsonPath("$.content.stats.median_lowest_price").value(93))
                // 트렌드 포인트 개수 및 값 확인
                .andExpect(jsonPath("$.content.stats.trends[0].time").value("2025-08-20T00:00:00Z"))
                .andExpect(jsonPath("$.content.stats.trends[0].lowest_price").value(90))
                .andExpect(jsonPath("$.content.stats.trends[0].total_quantity").value(2))
                .andExpect(jsonPath("$.content.stats.trends[1].time").value("2025-08-21T00:00:00Z"))
                .andExpect(jsonPath("$.content.stats.trends[1].lowest_price").value(95))
                .andExpect(jsonPath("$.content.stats.trends[1].total_quantity").value(3));
    }

    @Test
    @DisplayName("잘못된 region 요청 시 400 Bad Request를 반환한다")
    void returnsBadRequestWhenRegionIsInvalidEnum() throws Exception {
        mockMvc.perform(get("/api/v1/auctions/12345")
                        .param("region", "INVALID")
                        .param("realmId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }

    @Test
    void returns400ForNullRequiredFields() throws Exception {
        mockMvc.perform(get("/api/v1/auctions/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("itemBonus가 잘못된 형식일 경우 400 Bad Request를 반환한다")
    void returns400ForInvalidItemBonus() throws Exception {
        mockMvc.perform(get("/api/v1/auctions/1")
                        .param("region", "US")
                        .param("realmId", "123")
                        .param("itemBonus", "abc:123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("itemBonus 형식이 잘못되었습니다."));
    }

    @Test
    @DisplayName("서버 예외 발생 시 500 Internal Server Error를 반환한다")
    void returns500ForUnhandledException() throws Exception {
        Mockito.when(getAuctionDetailUseCase.execute(any()))
                .thenThrow(new RuntimeException("DB 연결 실패"));

        mockMvc.perform(get("/api/v1/auctions/1")
                        .param("region", "US")
                        .param("realmId", "123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
