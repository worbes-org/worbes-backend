package com.worbes.auctionhousetracker.runner;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.exception.InitializationException;
import com.worbes.auctionhousetracker.service.ItemClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worbes.auctionhousetracker.service.ItemClassService.ITEM_CLASS_SIZE;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class ItemClassRunner implements CommandLineRunner {

    private final ItemClassService itemClassService;

    @Override
    public void run(String... args) {
        if(itemClassService.count() == ITEM_CLASS_SIZE) {
            log.info("✅ 모든 아이템 클래스가 이미 저장되어 있습니다.");
            return;
        }

        List<ItemClass> itemClasses = itemClassService.fetchItemClassesIndex();
        if (itemClasses == null || itemClasses.isEmpty()) {
            log.error("❌ 아이템 클래스를 가져오는 데 실패했습니다.");
            throw new InitializationException("아이템 클래스 초기화 실패");
        }

        itemClassService.saveAll(itemClasses);
        log.info("🎉 모든 아이템 클래스 초기화가 완료되었습니다");
    }
}
