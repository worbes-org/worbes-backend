package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {


    @Mock
    private RestApiClient restApiClient;

    @InjectMocks
    private ItemService itemService;


    @Test
    @DisplayName("아이템 정보 수집 - 성공")
    void collectItemSuccess() {

    }
}
