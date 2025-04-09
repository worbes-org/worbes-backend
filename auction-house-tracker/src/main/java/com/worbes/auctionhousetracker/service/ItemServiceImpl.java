package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.mapper.ItemSaveCommand;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemClassService itemClassService;
    private final ItemSubclassService itemSubclassService;
    private final ItemRepository itemRepository;

    @Override
    public Item get(Long itemId) {
        return itemRepository.findById(itemId).orElse(null);
    }

    @Override
    public Map<Long, Item> getItemsBy(Collection<Long> itemIds) {
        return itemRepository.findAllByIdIn(new HashSet<>(itemIds)).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
    }

    @Override
    public Set<Long> findMissingItemIds(Collection<Long> itemIds) {
        Set<Long> allItemIds = new HashSet<>(itemIds);
        Set<Long> existIds = itemRepository.findItemIdByItemIdIn(allItemIds);
        allItemIds.removeAll(existIds);
        log.info("🧩 누락 아이템 탐지 완료 [누락={}개]", allItemIds.size());
        return allItemIds;
    }

    @Override
    public void saveAll(List<ItemSaveCommand> commands) {
        List<Item> items = commands.stream()
                .map(dto -> {
                    ItemClass itemClass = itemClassService.get(dto.getItemClassId());
                    ItemSubclass itemSubclass = itemSubclassService.get(itemClass, dto.getItemSubclassId());
                    return Item.from(dto, itemClass, itemSubclass);
                })
                .toList();
        itemRepository.batchInsertIgnoreConflicts(items);
    }
}
