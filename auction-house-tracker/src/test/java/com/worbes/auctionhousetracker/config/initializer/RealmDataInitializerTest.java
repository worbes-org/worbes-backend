package com.worbes.auctionhousetracker.config.initializer;

import com.worbes.auctionhousetracker.service.RealmService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RealmDataInitializerTest {

    @Mock
    private RealmService realmService;

    @InjectMocks
    private RealmDataInitializer realmDataInitializer;

}
