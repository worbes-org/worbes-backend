package com.worbes.application.item.port.in;

import com.worbes.application.item.model.Item;

import java.util.List;
import java.util.Set;

public interface FetchItemUseCase {

    List<Item> fetchItemAsync(Set<Long> itemIds) throws InterruptedException;
}
