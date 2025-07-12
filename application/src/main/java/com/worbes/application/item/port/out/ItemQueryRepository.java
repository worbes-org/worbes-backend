package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemQueryRepository {
    List<Item> findByCondition(ItemSearchCondition condition);

    Optional<Item> findById(Long itemId);
}
