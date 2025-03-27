package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.BlizzardApiClient;
import com.worbes.auctionhousetracker.service.RealmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RealmDataInitializerTest {

    @Mock
    private RealmService realmService;

    @Mock
    private BlizzardApiClient blizzardApiClient;

    private ThreadPoolTaskExecutor asyncExecutor;

    private RealmDataInitializer realmDataInitializer;

    @BeforeEach
    void setUp() {
        for (Region region : Region.values()) {
            given(blizzardApiClient.fetchRealmIndex(region)).willReturn(new RealmIndexResponse());
        }

        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setCorePoolSize(10);
        asyncExecutor.initialize();

        realmDataInitializer = new RealmDataInitializer(realmService, blizzardApiClient, asyncExecutor);
    }
}
