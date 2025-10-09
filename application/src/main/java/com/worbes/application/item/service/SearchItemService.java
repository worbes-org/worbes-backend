package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemQuery;
import com.worbes.application.item.port.in.SearchItemUseCase;
import com.worbes.application.item.port.out.FindItemPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchItemService implements SearchItemUseCase {

    private final FindItemPort findItemPort;

    @Override
    public List<Item> execute(SearchItemQuery query) {
        return findItemPort.findBy(query)
                .stream()
                .toList();
    }
}
