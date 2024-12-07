package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.service.ItemClassService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DataConfigTest {

    @Autowired
    private ItemClassService itemClassService;

    @Test
    void testLoadItemClasses() {
        // When
        List<ItemClass> itemClasses = itemClassService.getAllItemClasses();  // 실제 DB에서 아이템 클래스 조회

        // Then
        assertEquals(14, itemClasses.size(), "아이템 클래스의 개수가 일치해야 합니다.");
    }
}
