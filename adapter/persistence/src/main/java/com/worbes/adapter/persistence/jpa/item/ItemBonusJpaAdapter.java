package com.worbes.adapter.persistence.jpa.item;

import com.worbes.application.item.model.ItemBonus;
import com.worbes.application.item.port.out.ReadItemBonusPort;
import com.worbes.application.item.port.out.SaveItemBonusPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemBonusJpaAdapter implements SaveItemBonusPort, ReadItemBonusPort {

    private final ItemBonusJpaRepository jpaRepository;

    @Override
    public void saveAll(List<ItemBonus> itemBonuses) {
        List<ItemBonusEntity> entities = itemBonuses.stream()
                .map(ItemBonusEntity::from)
                .toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
