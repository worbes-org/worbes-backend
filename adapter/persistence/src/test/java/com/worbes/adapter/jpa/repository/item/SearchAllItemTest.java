package com.worbes.adapter.jpa.repository.item;

import com.worbes.adapter.jpa.entity.ItemEntity;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.item.port.in.SearchItemCommand;
import com.worbes.application.item.port.out.CreateItemRepository;
import com.worbes.application.item.port.out.SearchItemRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Integration::ItemRepository::SearchAll")
class SearchAllItemTest {

    @Autowired
    private SearchItemRepository searchItemRepository;

    @Autowired
    private CreateItemRepository createItemRepository;

    @Autowired
    private ItemJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        List<Item> items = List.of(
                Item.builder()
                        .id(1L)
                        .name(Map.of(
                                "en_us", "Rugged Trapper's Boots",
                                "ko_kr", "주름진 덫사냥꾼용 장화"
                        ))
                        .itemClassId(1L)
                        .itemSubclassId(2L)
                        .quality(QualityType.UNCOMMON)
                        .level(20)
                        .inventoryType(InventoryType.FEET)
                        .previewItem(Map.of("url1", "url2"))
                        .iconUrl("url1")
                        .build(),

                Item.builder()
                        .id(2L)
                        .name(Map.of(
                                "en_us", "Smooth Leather Gloves",
                                "ko_kr", "부드러운 가죽 장갑"
                        ))
                        .itemClassId(1L)
                        .itemSubclassId(2L)
                        .quality(QualityType.RARE)
                        .level(22)
                        .inventoryType(InventoryType.HAND)
                        .previewItem(Map.of("url1", "url2"))
                        .iconUrl("url2")
                        .build(),

                Item.builder()
                        .id(3L)
                        .name(Map.of(
                                "en_us", "Reinforced Leather Helm",
                                "ko_kr", "강화 가죽 투구"
                        ))
                        .itemClassId(1L)
                        .itemSubclassId(2L)
                        .quality(QualityType.UNCOMMON)
                        .level(25)
                        .inventoryType(InventoryType.HEAD)
                        .previewItem(Map.of("url1", "url2"))
                        .iconUrl("url3")
                        .build(),

                Item.builder()
                        .id(4L)
                        .name(Map.of(
                                "en_us", "Trapper's Leather Vest",
                                "ko_kr", "덫사냥꾼용 가죽 조끼"
                        ))
                        .itemClassId(1L)
                        .itemSubclassId(2L)
                        .quality(QualityType.RARE)
                        .level(30)
                        .inventoryType(InventoryType.CHEST)
                        .previewItem(Map.of("url1", "url2"))
                        .iconUrl("url4")
                        .build()
        );

        createItemRepository.saveAll(items);
        entityManager.flush();
    }

    @Test
    @DisplayName("이름에 '가죽'을 포함하는 아이템을 검색하면 3개의 결과가 반환된다")
    void shouldFindAllItemsWithKoreanKeywordLeather() {
        List<ItemEntity> all = jpaRepository.findAll();
        // given
        SearchItemCommand command = new SearchItemCommand(null, null, "가죽");

        // when
        List<Item> results = searchItemRepository.searchAll(command);

        // then
        assertThat(results)
                .hasSize(3)
                .allSatisfy(item ->
                        assertThat(item.getName().values()).anySatisfy(
                                name -> assertThat(name).contains("가죽")
                        )
                );
    }

    @Test
    @DisplayName("이름에 '덫사냥꾼'이 포함된 아이템 2개를 모두 검색한다")
    void shouldFindTwoItemsWithKeywordTrapperInKorean() {
        // given
        SearchItemCommand command = new SearchItemCommand(null, null, "덫사냥꾼");

        // when
        List<Item> results = searchItemRepository.searchAll(command);

        // then
        assertThat(results)
                .hasSize(2)
                .allSatisfy(item ->
                        assertThat(item.getName().get("ko_kr")).contains("덫사냥꾼")
                );
    }

    @Test
    @DisplayName("이름에 'trapper'를 포함하는 아이템 2개가 검색된다")
    void shouldFindTwoItemsWithKeywordTrapperInEnglish() {
        // given
        SearchItemCommand command = new SearchItemCommand(null, null, "trapper");

        // when
        List<Item> results = searchItemRepository.searchAll(command);

        // then
        assertThat(results)
                .hasSize(2)
                .allSatisfy(item ->
                        assertThat(item.getName().get("en_us").toLowerCase()).contains("trapper")
                );
    }

    @Test
    @DisplayName("이름에 '없는단어'를 검색하면 결과가 비어야 한다")
    void shouldReturnEmptyWhenNameDoesNotMatch() {
        // given
        SearchItemCommand command = new SearchItemCommand(
                null, null, "없는단어"
        );

        // when
        List<Item> results = searchItemRepository.searchAll(command);

        // then
        assertThat(results).isEmpty();
    }
}
