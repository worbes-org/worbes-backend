package com.worbes.application.item.port.out;

import com.worbes.application.item.model.Item;

import java.util.List;

public interface ItemCommandRepository {
    List<Item> saveAll(List<Item> items);
}
