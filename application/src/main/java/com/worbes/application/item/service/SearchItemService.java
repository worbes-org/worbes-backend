package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchAllItemUseCase;
import com.worbes.application.item.port.in.SearchItemCommand;
import com.worbes.application.item.port.out.SearchItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchItemService implements SearchAllItemUseCase {

    private final SearchItemRepository searchItemRepository;

    @Override
    public List<Item> searchAll(SearchItemCommand command) {
        return searchItemRepository.search(command);
    }
}
