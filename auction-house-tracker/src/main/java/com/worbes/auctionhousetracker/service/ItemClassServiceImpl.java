package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassServiceImpl implements ItemClassService {

    private final ItemClassRepository itemClassRepository;

    @Getter
    private final Set<Long> requiredItemClasses = Set.of(
            0L, 1L, 2L, 3L, 4L, 5L, 8L, 9L, 12L, 15L, 16L, 17L, 18L, 19L
    );

    @Transactional
    public void save(ItemClassesIndexResponse response) {
        List<ItemClass> itemClasses = response.getItemClassDtos().stream()
                .filter(dto -> requiredItemClasses.contains(dto.getId()))
                .map(ItemClass::create)
                .toList();
        itemClassRepository.saveAll(itemClasses);
    }

    public boolean isRequiredItemClassesExist() {
        // DB에서 존재하는 itemClassId 조회
        List<Long> existingIds = itemClassRepository.findExistingItemClassIds(requiredItemClasses);

        // 존재하는 ID를 HashSet으로 변환
        Set<Long> foundIds = new HashSet<>(existingIds);

        // 존재하지 않는 ID 찾기
        Set<Long> missingIds = new HashSet<>(requiredItemClasses);
        missingIds.removeAll(foundIds);

        if (!missingIds.isEmpty()) {
            log.info("ItemClasses are missing: {}", missingIds);
            return false;
        }
        return true;
    }
}
