package com.worbes.adapter.persistence.mybatis.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemQuery;
import com.worbes.application.item.port.out.FindItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemMybatisAdapter implements FindItemPort {

    private final ItemMybatisMapper mapper;

    @Override
    public List<Item> findBy(SearchItemQuery query) {
        return mapper.findBy(query)
                .stream()
                .map(dto -> Item.builder()
                        .id(dto.id())
                        .name(dto.name())
                        .classId(dto.classId())
                        .subclassId(dto.subclassId())
                        .level(dto.level())
                        .icon(dto.icon())
                        .expansionId(dto.expansionId())
                        .displayId(dto.displayId())
                        .inventoryType(dto.inventoryType())
                        .craftingTier(dto.craftingTier())
                        .quality(dto.quality())
                        .isStackable(dto.isStackable())
                        .build()
                )
                .toList();
    }
}
