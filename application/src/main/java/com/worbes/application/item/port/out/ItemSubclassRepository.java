package com.worbes.application.item.port.out;


import com.worbes.application.item.model.ItemSubclass;

import java.util.Optional;

public interface ItemSubclassRepository {
    Optional<ItemSubclass> findById(Long itemClassId, Long subclassId);

    ItemSubclass save(ItemSubclass itemSubclass);
}
