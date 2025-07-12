package com.worbes.application.bonus.port.out;

import com.worbes.application.bonus.port.model.ItemBonus;

import java.util.List;

public interface BonusCommandRepository {
    void saveAll(List<ItemBonus> itemBonuses);
}
