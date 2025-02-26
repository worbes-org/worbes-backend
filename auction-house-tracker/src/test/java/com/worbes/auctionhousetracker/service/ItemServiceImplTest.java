package com.worbes.auctionhousetracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.enums.NamespaceType;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    @Mock
    private RestApiClient restApiClient;

    @InjectMocks
    private ItemServiceImpl itemService;


    @Test
    @DisplayName("아이템 정보 수집 - 성공")
    void collectItemSuccess() throws IOException {
        Region region = Region.US;
        ObjectMapper objectMapper = new ObjectMapper();
        ItemResponse itemResponse = objectMapper.readValue(
                getClass().getResourceAsStream("/json/item-response.json"),
                ItemResponse.class
        );
        given(restApiClient.get(anyString(), anyMap(), eq(ItemResponse.class))).willReturn(itemResponse);

        Item item = itemService.fetchItem(itemResponse.getId());

        verify(restApiClient, times(1))
                .get(
                        eq(BlizzardApiUrlBuilder.builder(region).item(itemResponse.getId()).build()),
                        eq(BlizzardApiParamsBuilder.builder(region).namespace(NamespaceType.STATIC).build()),
                        eq(ItemResponse.class)
                );
    }
}
