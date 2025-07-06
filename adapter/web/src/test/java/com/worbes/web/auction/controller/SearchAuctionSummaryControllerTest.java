package com.worbes.web.auction.controller;

import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.AuctionSummary;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.item.port.in.SearchItemUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchAuctionSummaryController.class)
class SearchAuctionSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchItemUseCase searchItemUseCase;

    @MockBean
    private SearchAuctionSummaryUseCase searchAuctionSummaryUseCase;

    @Test
    @DisplayName("주어진 유효한 파라미터로 GET /api/v1/auctions 호출 시 200 OK와 결과를 반환한다")
    void returnsOkWithValidParameters() throws Exception {
        // given
        Item item = Item.builder()
                .id(1L)
                .name(Map.of("ko_KR", "검", "en_US", "Sword"))
                .itemClassId(2L)
                .itemSubclassId(1L)
                .quality(QualityType.EPIC)
                .level(100)
                .inventoryType(InventoryType.WEAPON)
                .previewItem(null)
                .iconUrl("http://image.url")
                .craftingTier(CraftingTierType.FIRST)
                .build();
        AuctionSummary auctionSummary = new AuctionSummary(item.getId(), 123456L, 5);
        given(searchItemUseCase.search(any())).willReturn(List.of(item));
        given(searchAuctionSummaryUseCase.searchSummaries(any())).willReturn(List.of(auctionSummary));

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions")
                .param("region", "KR")
                .param("realmId", "2116")
                .param("itemClassId", "2")
                .param("itemSubclassId", "1")
                .param("itemName", "검")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name.ko_KR").value("검"))
                .andExpect(jsonPath("$.content[0].name.en_US").value("Sword"))
                .andExpect(jsonPath("$.content[0].iconUrl").value("http://image.url"))
                .andExpect(jsonPath("$.content[0].tier").value(1))
                .andExpect(jsonPath("$.content[0].available").value(5))
                .andExpect(jsonPath("$.content[0].lowestPrice").value(123456L));
    }

    @Test
    @DisplayName("필수 파라미터가 모두 없을 때 400 Bad Request를 반환한다")
    void returnsBadRequestWhenMissingAllRequiredParameters() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("itemName이 빈 문자열일 때 400 Bad Request를 반환한다")
    void returnsBadRequestWhenItemNameIsBlank() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions")
                .param("region", "KR")
                .param("realmId", "2116")
                .param("itemName", "")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("itemName이 100자를 초과할 때 400 Bad Request를 반환한다")
    void returnsBadRequestWhenItemNameExceedsMaxLength() throws Exception {
        // when
        String longItemName = "a".repeat(101);
        ResultActions result = mockMvc.perform(get("/api/v1/auctions")
                .param("region", "KR")
                .param("realmId", "2116")
                .param("itemName", longItemName)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("region 파라미터가 없을 때 400 Bad Request를 반환한다")
    void returnsBadRequestWhenRegionIsMissing() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions")
                .param("realmId", "2116")
                .param("itemName", "검")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("realmId 파라미터가 없을 때 400 Bad Request를 반환한다")
    void returnsBadRequestWhenRealmIdIsMissing() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/auctions")
                .param("region", "KR")
                .param("itemName", "검")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }
} 
