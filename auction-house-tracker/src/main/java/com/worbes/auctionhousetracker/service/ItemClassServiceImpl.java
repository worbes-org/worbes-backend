package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.config.properties.RequiredItemClassesProperties;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassServiceImpl implements ItemClassService {

    private final ItemClassRepository itemClassRepository;
    private final RequiredItemClassesProperties properties;

    @Override
    public ItemClass get(Long id) {
        return itemClassRepository.findById(id).orElseThrow(() -> new NoSuchElementException("ItemClass not found"));
    }

    @Override
    @Transactional
    public void saveRequiredClass(ItemClassesIndexResponse response) {
        List<ItemClass> itemClasses = response.getItemClassDtos().stream()
                .filter(dto -> properties.getRequiredClasses().containsKey(dto.getId()))
                .map(ItemClass::create)
                .toList();
        itemClassRepository.saveAll(itemClasses);
    }

    @Override
    public Set<Long> getMissingItemClasses() {
        // 1. 설정에서 필요한 아이템 클래스 목록 가져오기
        Set<Long> requiredClassIds = getRequiredClassIds();

        // 2. 현재 DB에 존재하는 아이템 클래스 조회
        Set<Long> existingClassIds = getExistingClassIds(requiredClassIds);

        // 3. 누락된 아이템 클래스 찾기
        return findMissingClasses(requiredClassIds, existingClassIds);
    }

    /**
     * 설정에서 필요한 아이템 클래스 ID 목록을 가져옴.
     */
    private Set<Long> getRequiredClassIds() {
        Set<Long> requiredClassIds = properties.getRequiredClasses().keySet();
        if (requiredClassIds.isEmpty()) {
            throw new IllegalStateException("Required classes are not set");
        }
        return requiredClassIds;
    }

    /**
     * DB에서 현재 존재하는 아이템 클래스 목록을 조회하여 반환.
     */
    private Set<Long> getExistingClassIds(Set<Long> classIds) {
        List<ItemClass> existingClasses = itemClassRepository.findItemClassesByIds(classIds);
        return existingClasses.stream()
                .map(ItemClass::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 필요한 클래스 목록과 현재 존재하는 클래스를 비교하여 누락된 클래스를 찾음.
     */
    private Set<Long> findMissingClasses(Set<Long> required, Set<Long> existing) {
        Set<Long> missingClasses = new HashSet<>(required);
        missingClasses.removeAll(existing);

        if (!missingClasses.isEmpty()) {
            log.warn("🚨 누락된 아이템 클래스: {}", missingClasses);
        }

        return missingClasses;
    }
}
