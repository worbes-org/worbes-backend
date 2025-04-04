package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.mapper.ItemSaveDto;
import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Item;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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
    public Set<Long> findMissingItemIds(AuctionResponse response) {
        // 1. 경매 응답에서 아이템 ID만 모으기
        Set<Long> allItemIds = response.getAuctions().stream()
                .map(AuctionResponse.AuctionDto::getItemId)
                .collect(Collectors.toSet());
        // 2. DB에서 존재하는 ID 조회
        Set<Long> existingIds = itemRepository.findAllById(allItemIds).stream()
                .map(Item::getId)
                .collect(Collectors.toSet());
        // 3. 없는 ID만 필터링
        allItemIds.removeAll(existingIds); // 이 줄로 차집합
        return allItemIds; // 이게 너가 API로 가져와야 할 놈들
    }

    @Override
    public void save(List<ItemSaveDto> dtos) {
        List<Item> items = dtos.stream().map(dto -> {
            ItemClass itemClass = itemClassService.get(dto.getItemClassId());
            ItemSubclass itemSubclass = itemSubclassService.get(itemClass, dto.getItemSubclassId());
            return Item.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .itemClass(itemClass)
                    .itemSubclass(itemSubclass)
                    .iconUrl(dto.getIconUrl())
                    .level(dto.getLevel())
                    .inventoryType(dto.getInventoryType())
                    .previewItem(dto.getPreviewItem())
                    .quality(dto.getQuality())
                    .build();
        }).toList();
        itemRepository.saveAll(items);
    }
}
