package com.worbes.application.item.service;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemCondition;
import com.worbes.application.item.port.in.SearchItemUseCase;
import com.worbes.application.item.port.out.ItemReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemReadService implements SearchItemUseCase {

    private final ItemReadRepository itemReadRepository;

    @Override
    public List<Item> search(SearchItemCondition condition) {
        return itemReadRepository.findAllByCondition(condition);
    }
}
