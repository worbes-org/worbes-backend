package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.properties.RequiredItemClassesProperties;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemSubclassServiceImpl implements ItemSubclassService {

    private final ItemSubclassRepository itemSubclassRepository;
    private final ItemClassService itemClassService;
    private final RequiredItemClassesProperties properties;

    @Override
    public ItemSubclass get(ItemClass itemClass, Long itemSubclassId) {
        return itemSubclassRepository.findByItemClassAndSubclassId(itemClass, itemSubclassId)
                .orElse(null);
    }

    @Override
    @Transactional
    public void saveRequiredSubclass(List<ItemSubclassResponse> responses, Long itemClassId) {
        ItemClass itemClass = itemClassService.get(itemClassId);
        List<ItemSubclass> subclasses = responses.stream()
                .filter(this::isNecessarySubclass)
                .map(res -> ItemSubclass.create(res, itemClass))
                .toList();
        itemSubclassRepository.saveAll(subclasses);
    }

    private boolean isNecessarySubclass(ItemSubclassResponse response) {
        return properties.getRequiredClasses()
                .get(response.getClassId())
                .contains(response.getId());
    }

    @Override
    public Map<Long, Set<Long>> getMissingItemSubclasses() {
        // 1. 설정에서 필요한 아이템 클래스와 서브클래스 ID 목록 가져오기
        Map<Long, Set<Long>> requiredClasses = getRequiredClasses();

        // 2. 현재 DB에 존재하는 서브클래스 조회
        Map<Long, Set<Long>> existingSubclasses = getExistingSubclasses(requiredClasses.keySet());

        // 3. 누락된 서브클래스 찾기
        return findMissingSubclasses(requiredClasses, existingSubclasses);
    }

    /**
     * required-classes 설정에서 필요한 아이템 클래스 ID와 서브클래스 ID 목록을 가져옴.
     */
    private Map<Long, Set<Long>> getRequiredClasses() {
        Map<Long, Set<Long>> requiredClasses = properties.getRequiredClasses();
        if (requiredClasses == null || requiredClasses.isEmpty()) {
            throw new IllegalStateException("Required classes are not set");
        }
        return requiredClasses;
    }

    /**
     * DB에서 현재 존재하는 아이템 서브클래스 목록을 조회하여 반환.
     */
    private Map<Long, Set<Long>> getExistingSubclasses(Set<Long> classIds) {
        List<ItemSubclass> existingSubclasses = itemSubclassRepository.findByItemClassIdIn(classIds);

        return existingSubclasses.stream()
                .collect(Collectors.groupingBy(
                        subclass -> subclass.getItemClass().getId(),
                        Collectors.mapping(ItemSubclass::getSubclassId, Collectors.toSet())
                ));
    }

    /**
     * 필요한 서브클래스 목록과 현재 존재하는 서브클래스를 비교하여 누락된 서브클래스를 찾음.
     */
    private Map<Long, Set<Long>> findMissingSubclasses(Map<Long, Set<Long>> required, Map<Long, Set<Long>> existing) {
        Map<Long, Set<Long>> missingSubclasses = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> entry : required.entrySet()) {
            Long itemClassId = entry.getKey();
            Set<Long> requiredSubclasses = entry.getValue();
            Set<Long> existingSubclassesForClass = existing.getOrDefault(itemClassId, new HashSet<>());
            Set<Long> missing = new HashSet<>(requiredSubclasses);
            missing.removeAll(existingSubclassesForClass);

            if (!missing.isEmpty()) {
                missingSubclasses.put(itemClassId, missing);
            }
        }

        return missingSubclasses;
    }
}
