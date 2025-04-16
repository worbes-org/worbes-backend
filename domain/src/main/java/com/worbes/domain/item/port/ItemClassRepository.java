package com.worbes.domain.item.port;

import com.worbes.domain.item.ItemClass;

import java.util.List;
import java.util.Optional;

public interface ItemClassRepository {
    Optional<ItemClass> findBy(Long id);

    List<ItemClass> findAllBy(Iterable<Long> classIds);

    void save(ItemClass itemClass);

    void saveAll(Iterable<ItemClass> itemClasses);
}
