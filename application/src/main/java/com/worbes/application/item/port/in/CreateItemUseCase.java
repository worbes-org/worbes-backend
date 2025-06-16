package com.worbes.application.item.port.in;

import com.worbes.application.item.model.Item;

import java.util.List;

public interface CreateItemUseCase {
    List<Item> saveAll(List<Item> items);
}
