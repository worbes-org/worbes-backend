package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.service.ItemClassService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ItemClassDataLoaderTest {

    @Autowired
    private ItemClassService itemClassService;

    @Autowired
    private CommandLineRunner itemClassDataLoader;

    @Test
    void testLoadItemClasses() {
        // Given: 데이터 로딩이 실행되었을 때 (ItemClassDataLoader 실행)
        // @SpringBootTest가 자동으로 CommandLineRunner를 실행하여 데이터를 로드합니다.

        // When: 데이터베이스에서 모든 아이템 클래스 조회
        List<ItemClass> itemClasses = itemClassService.getAllItemClasses();  // 실제 DB에서 아이템 클래스 조회

        // Then: 데이터베이스에 14개의 아이템 클래스가 저장되어 있어야 한다.
        ItemClassDataLoader loader = (ItemClassDataLoader) itemClassDataLoader;
        assertEquals(loader.getItemClasses().size(), itemClasses.size(), "아이템 클래스의 개수가 일치해야 합니다.");
    }
}
