package com.worbes.application.item.port.in;

import com.worbes.application.item.model.Item;

import java.util.List;

public interface SearchAllItemUseCase {
    List<Item> searchAll(SearchItemCommand command);
}
