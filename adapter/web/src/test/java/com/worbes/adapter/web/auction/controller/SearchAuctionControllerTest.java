package com.worbes.adapter.web.auction.controller;

import com.worbes.application.auction.port.in.SearchAuctionSummaryQuery;
import com.worbes.application.auction.port.in.SearchAuctionSummaryResult;
import com.worbes.application.auction.port.in.SearchAuctionUseCase;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.item.port.in.SearchItemQuery;
import com.worbes.application.item.port.in.SearchItemUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchAuctionController.class)
class SearchAuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchItemUseCase searchItemUseCase;

    @MockBean
    private SearchAuctionUseCase searchAuctionUseCase;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(1L)
                .name(Map.of("ko_KR", "검"))
                .classId(1L)
                .subclassId(2L)
                .quality(QualityType.EPIC)
                .level(50)
                .inventoryType(InventoryType.WEAPON)
                .isStackable(false)
                .icon("icon.png")
                .expansionId(1)
                .displayId(100L)
                .build();
    }

    @Test
    @DisplayName("정상 흐름: 아이템과 경매 데이터 모두 존재")
    void givenItemsAndAuctions_whenGetAuctions_thenReturnOk() throws Exception {
        // given
        given(searchItemUseCase.execute(any(SearchItemQuery.class)))
                .willReturn(List.of(testItem));

        given(searchAuctionUseCase.execute(any(SearchAuctionSummaryQuery.class)))
                .willReturn(List.of(
                        new SearchAuctionSummaryResult(
                                1L, List.of(100L, 200L), 50, null, 1000L, 10
                        )
                ));

        // when & then
        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "KR")
                        .param("realmId", "205")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].item_id").value(1))
                .andExpect(jsonPath("$.content[0].item_bonus").value("100:200"))
                .andExpect(jsonPath("$.content[0].item_level").value(50))
                .andExpect(jsonPath("$.content[0].crafting_tier").doesNotExist())
                .andExpect(jsonPath("$.content[0].lowest_price").value(1000))
                .andExpect(jsonPath("$.content[0].total_quantity").value(10))
                // hasNext 검증
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("아이템 없음: searchItemUseCase 빈 리스트 반환")
    void givenNoItems_whenGetAuctions_thenReturnEmptySlice() throws Exception {
        // given
        given(searchItemUseCase.execute(any(SearchItemQuery.class))).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "US")
                        .param("realmId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("경매 데이터 없음: 아이템 존재하지만 searchAuctionUseCase 빈 리스트 반환")
    void givenItemsButNoAuctions_whenGetAuctions_thenReturnEmptySlice() throws Exception {
        // given
        given(searchItemUseCase.execute(any(SearchItemQuery.class))).willReturn(List.of(testItem));
        given(searchAuctionUseCase.execute(any(SearchAuctionSummaryQuery.class))).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "US")
                        .param("realmId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 파라미터: 검증 실패 시 400 반환")
    void givenInvalidParameters_whenGetAuctions_thenReturnBadRequest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "")
                        .param("realmId", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("결과가 페이지 크기보다 적으면 hasNext false")
    void givenFewerResultsThanPageSize_whenGetSummary_thenHasNextFalse() throws Exception {
        given(searchItemUseCase.execute(any())).willReturn(List.of(testItem));
        given(searchAuctionUseCase.execute(any())).willReturn(List.of(
                new SearchAuctionSummaryResult(
                        1L, List.of(), 50, null, 500L, 5
                )
        ));

        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "US")
                        .param("realmId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.last").value(true)); // SliceImpl.hasNext=false
    }

    @Test
    @DisplayName("결과가 페이지 크기보다 많으면 Slice 자르고 hasNext true")
    void givenMoreResultsThanPageSize_whenGetSummary_thenHasNextTrue() throws Exception {
        List<SearchAuctionSummaryResult> auctions =
                List.of(
                        new SearchAuctionSummaryResult(
                                1L, List.of(), 50, null, 500L, 5
                        ),
                        new SearchAuctionSummaryResult(
                                2L, List.of(), 45, null, 400L, 3
                        )
                );

        given(searchItemUseCase.execute(any())).willReturn(List.of(testItem));
        given(searchAuctionUseCase.execute(any())).willReturn(auctions);

        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "US")
                        .param("realmId", "1")
                        .param("classId", "1")
                        .param("size", "1") // Pageable size = 1
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.last").value(false)); // hasNext=true
    }

    @Test
    @DisplayName("필수 값 누락 시 400 Bad Request")
    void givenMissingRequiredParam_whenGetSummary_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "")
                        .param("realmId", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // 4-1. searchItemUseCase 예외
    @Test
    @DisplayName("searchItemUseCase 예외 시 500 반환")
    void givenSearchItemThrows_whenGetSummary_thenInternalServerError() throws Exception {
        given(searchItemUseCase.execute(any())).willThrow(new RuntimeException("Item Error"));

        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "US")
                        .param("realmId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // 4-2. searchAuctionUseCase 예외
    @Test
    @DisplayName("searchAuctionUseCase 예외 시 500 반환")
    void givenSearchAuctionThrows_whenGetSummary_thenInternalServerError() throws Exception {
        given(searchItemUseCase.execute(any())).willReturn(List.of(testItem));
        given(searchAuctionUseCase.execute(any())).willThrow(new RuntimeException("Auction Error"));

        mockMvc.perform(get("/api/v1/auctions")
                        .param("region", "US")
                        .param("realmId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
