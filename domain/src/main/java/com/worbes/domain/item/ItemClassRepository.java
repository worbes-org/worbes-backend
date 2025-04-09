package com.worbes.domain.item;

import java.util.Optional;

public interface ItemClassRepository {
    Optional<ItemClass> findBy(Long id);

    void save(ItemClass itemClass);

    void saveAll(Iterable<ItemClass> itemClasses);
}
