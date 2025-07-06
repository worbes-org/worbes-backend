package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemCondition;

import java.util.List;

public interface ItemReadRepository {
    List<Item> findAllByCondition(SearchItemCondition command);

    Item findById(Long itemId);
}
