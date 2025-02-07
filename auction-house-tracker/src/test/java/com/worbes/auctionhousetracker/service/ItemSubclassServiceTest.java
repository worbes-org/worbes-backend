package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.client.BlizzardRestClient;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemSubclassServiceTest {

    @Mock
    ItemSubclassRepository itemSubclassRepository;

    @Mock
    BlizzardRestClient restClient;

    @Mock
    Bucket bucket;

    @InjectMocks
    ItemSubclassService itemSubclassService;
}
