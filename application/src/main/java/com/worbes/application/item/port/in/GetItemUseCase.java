package com.worbes.application.item.port.in;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemSearchCondition;

import java.util.List;

public interface GetItemUseCase {
    Item get(Long itemId);

    List<Item> get(ItemSearchCondition condition);
}
