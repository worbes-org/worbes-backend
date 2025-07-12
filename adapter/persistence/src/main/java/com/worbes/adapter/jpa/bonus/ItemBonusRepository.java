package com.worbes.adapter.jpa.bonus;

import com.worbes.application.bonus.port.model.ItemBonus;
import com.worbes.application.bonus.port.out.BonusCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemBonusRepository implements BonusCommandRepository {

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
