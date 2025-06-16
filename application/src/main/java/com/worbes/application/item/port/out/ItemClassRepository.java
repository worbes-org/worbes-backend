package com.worbes.application.item.port.out;


import com.worbes.application.item.model.ItemClass;

import java.util.List;
import java.util.Optional;

public interface ItemClassRepository {
    Optional<ItemClass> findById(Long id);

    List<ItemClass> findAllById(Iterable<Long> classIds);

    List<ItemClass> findAll();

    ItemClass save(ItemClass itemClass);

    void saveAll(Iterable<ItemClass> itemClasses);

    void update(ItemClass itemClass);
}
