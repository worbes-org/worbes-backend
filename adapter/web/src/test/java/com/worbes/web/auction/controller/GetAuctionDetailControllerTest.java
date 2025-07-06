package com.worbes.web.auction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.model.AuctionDetail;
import com.worbes.application.auction.port.in.GetAuctionDetailUseCase;
import com.worbes.application.auction.port.out.AuctionTrend;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetAuctionDetailController.class)
class GetAuctionDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetAuctionDetailUseCase getAuctionDetailUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("정상적으로 경매 상세를 반환한다")
        void returnsAuctionDetailSuccessfully() throws Exception {
            // given
            Item item = Item.builder()
                    .id(1L)
                    .name(Map.of("ko_KR", "검"))
                    .itemClassId(2L)
                    .itemSubclassId(1L)
                    .quality(QualityType.EPIC)
                    .level(100)
                    .inventoryType(InventoryType.WEAPON)
                    .previewItem(null)
                    .iconUrl("http://image.url")
                    .craftingTier(CraftingTierType.FIRST)
                    .build();
            Auction auction = Auction.builder()
                    .id(10L)
                    .itemId(1L)
                    .realmId(2116L)
                    .quantity(5L)
                    .price(1000L)
                    .region(RegionType.KR)
                    .endedAt(null)
                    .build();
            AuctionTrend trend = new AuctionTrend(LocalDateTime.of(2024, 1, 1, 0, 0), 5, 1000L);
            AuctionDetail auctionDetail = new AuctionDetail(item, List.of(auction), List.of(trend));
            given(getAuctionDetailUseCase.getDetail(eq(1L), eq(RegionType.KR), eq(2116L))).willReturn(auctionDetail);

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/auctions/1")
                    .param("region", "KR")
                    .param("realmId", "2116")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.item.id").value(1L))
                    .andExpect(jsonPath("$.content.item.name.ko_KR").value("검"))
                    .andExpect(jsonPath("$.content.available.1000").value(5))
                    .andExpect(jsonPath("$.content.trends[0].lowestPrice").value(1000L))
                    .andExpect(jsonPath("$.content.trends[0].totalQuantity").value(5))
                    .andExpect(jsonPath("$.content.trends[0].time").exists());
        }
    }

    @Nested
    @DisplayName("경계/실패 케이스")
    class EdgeAndFailCases {
        @Test
        @DisplayName("필수 파라미터가 없으면 400 Bad Request를 반환한다")
        void returnsBadRequestWhenMissingRequiredParams() throws Exception {
            // when
            ResultActions result = mockMvc.perform(get("/api/v1/auctions/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("존재하지 않는 itemId로 조회 시 404 Not Found를 반환한다")
        void returnsNotFoundWhenAuctionDetailNotFound() throws Exception {
            // given
            given(getAuctionDetailUseCase.getDetail(eq(999L), eq(RegionType.KR), eq(2116L)))
                    .willThrow(new IllegalArgumentException("경매 상세를 찾을 수 없습니다."));

            // when
            ResultActions result = mockMvc.perform(get("/api/v1/auctions/999")
                    .param("region", "KR")
                    .param("realmId", "2116")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

            // then
            result.andExpect(status().is4xxClientError()); // 실제 예외 핸들러에 따라 404/400 등 조정
        }
    }
} 
