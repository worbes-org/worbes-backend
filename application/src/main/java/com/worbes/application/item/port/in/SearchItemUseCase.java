package com.worbes.application.item.port.in;

import com.worbes.application.item.model.Item;

import java.util.List;

public interface SearchItemUseCase {
    List<Item> execute(SearchItemQuery query);
}
