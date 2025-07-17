package com.worbes.adapter.jpa.item;

import com.worbes.application.item.model.ItemBonus;
import com.worbes.application.item.port.out.SaveItemBonusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemBonusRepository implements SaveItemBonusPort {

    private final ItemBonusJpaRepository jpaRepository;

    @Override
    public void saveAll(List<ItemBonus> itemBonuses) {
        List<ItemBonusEntity> entities = itemBonuses.stream()
                .map(itemBonus -> new ItemBonusEntity(
                                itemBonus.id(),
                                itemBonus.suffix(),
                                itemBonus.level(),
                                itemBonus.baseLevel()
                        )
                )
                .toList();

        jpaRepository.saveAll(entities);
    }
}
