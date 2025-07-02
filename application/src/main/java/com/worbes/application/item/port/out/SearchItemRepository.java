package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemCommand;

import java.util.List;

public interface SearchItemRepository {
    List<Item> search(SearchItemCommand command);
}
