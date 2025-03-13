package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ItemClassServiceTest {

    @Mock
    private ItemClassRepository itemClassRepository;

    @Mock
    private RestApiClient restApiClient;

    @InjectMocks
    private ItemClassService itemClassService;

}
