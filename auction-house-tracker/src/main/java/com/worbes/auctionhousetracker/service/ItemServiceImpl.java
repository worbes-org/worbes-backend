package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final RestApiClient restApiClient;

    @Override
    public Item fetchItem(Long id) {
        return null;
    }

}
