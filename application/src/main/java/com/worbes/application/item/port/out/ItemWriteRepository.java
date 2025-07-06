package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;

import java.util.List;

public interface ItemWriteRepository {
    List<Item> saveAll(List<Item> items);
}
