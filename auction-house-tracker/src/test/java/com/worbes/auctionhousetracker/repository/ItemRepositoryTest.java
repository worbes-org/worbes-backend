package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static com.worbes.auctionhousetracker.TestUtils.loadJsonResource;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Rollback(false)
    @DisplayName("item-response.json을 읽어 실제 DB에 저장 및 조회 테스트")
    void testSaveItem() throws Exception {
        // 1. JSON 파일 로드
        ItemResponse response = loadJsonResource("/json/item-response.json", ItemResponse.class);

        // 2. Item 엔티티 생성 (iconUrl은 예시로 "test-icon-url" 사용)
        Item item = Item.from(response, "test-icon-url");

        // 3. DB에 저장
        itemRepository.save(item);

        // 4. DB에서 재조회
        Item savedItem = itemRepository.findById(item.getId()).orElseThrow();

        // 5. 검증
        assertThat(savedItem.getId()).isEqualTo(response.getId());
        assertThat(savedItem.getPreviewItem()).isNotEmpty();  // preview_item이 jsonb로 저장되었는지 확인
        assertThat(savedItem.getIconUrl()).isEqualTo("test-icon-url");
    }
}
