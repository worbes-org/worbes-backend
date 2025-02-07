package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.client.BlizzardRestClient;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {
    public static final int ITEM_CLASS_SIZE = 18;
    private final ItemClassRepository itemClassRepository;
    private final BlizzardRestClient restClient;

    public Long count() {
        return itemClassRepository.count();
    }

    public ItemClass get(Long id) {
        return itemClassRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public List<ItemClass> getAll() {
        return itemClassRepository.findAll();
    }

    public void save(ItemClass itemClass) {
        itemClassRepository.save(itemClass);
    }

    public void saveAll(List<ItemClass> itemClasses) {
        itemClassRepository.saveAll(itemClasses);
    }

    public void init() {
        if(count() == ITEM_CLASS_SIZE) {
            log.info("모든 아이템 클래스가 이미 저장되어 있습니다.");
            return;
        }
        //TODO: API 호출 실패 시 처리(재시도..)
        //TODO: saveAll() 실패 시 처리
        saveAll(restClient.fetchItemClassesIndex());
        log.info("아이템 클래스 저장 완료");
    }
}
