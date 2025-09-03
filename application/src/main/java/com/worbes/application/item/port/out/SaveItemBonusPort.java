package com.worbes.application.item.port.out;

import com.worbes.application.item.model.ItemBonus;

import java.util.List;

public interface SaveItemBonusPort {
    void saveAll(List<ItemBonus> itemBonuses);
}
