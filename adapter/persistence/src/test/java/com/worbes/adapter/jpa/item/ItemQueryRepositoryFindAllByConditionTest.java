package com.worbes.adapter.jpa.item;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.item.port.out.ItemCommandRepository;
import com.worbes.application.item.port.out.ItemQueryRepository;
import com.worbes.application.item.port.out.ItemSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "item-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemQueryRepositoryFindAllByConditionTest {

    @Autowired
    private ItemQueryRepository itemQueryRepository;

    @Autowired
    private ItemCommandRepository itemCommandRepository;

    @BeforeEach
    void setUp() {
        List<Item> items = List.of(
                Item.builder()
                        .id(1L)
                        .name(Map.of(
                                LocaleCode.EN_US.getValue(), "Rugged Trapper's Boots",
                                LocaleCode.KO_KR.getValue(), "주름진 덫사냥꾼용 장화"
                        ))
                        .classId(1L)
                        .subclassId(2L)
                        .quality(QualityType.UNCOMMON)
                        .level(20)
                        .inventoryType(InventoryType.FEET)
                        .icon("url1")
                        .isStackable(false)
                        .build(),

                Item.builder()
                        .id(2L)
                        .name(Map.of(
                                LocaleCode.EN_US.getValue(), "Smooth Leather Gloves",
                                LocaleCode.KO_KR.getValue(), "부드러운 가죽 장갑"
                        ))
                        .classId(1L)
                        .subclassId(2L)
                        .quality(QualityType.RARE)
                        .level(22)
                        .inventoryType(InventoryType.HAND)
                        .icon("url2")
                        .isStackable(false)
                        .build(),

                Item.builder()
                        .id(3L)
                        .name(Map.of(
                                LocaleCode.EN_US.getValue(), "Reinforced Leather Helm",
                                LocaleCode.KO_KR.getValue(), "강화 가죽 투구"
                        ))
                        .classId(1L)
                        .subclassId(2L)
                        .quality(QualityType.UNCOMMON)
                        .level(25)
                        .inventoryType(InventoryType.HEAD)
                        .icon("url3")
                        .isStackable(false)
                        .build(),

                Item.builder()
                        .id(4L)
                        .name(Map.of(
                                LocaleCode.EN_US.getValue(), "Trapper's Leather Vest",
                                LocaleCode.KO_KR.getValue(), "덫사냥꾼용 가죽 조끼"
                        ))
                        .classId(1L)
                        .subclassId(2L)
                        .quality(QualityType.RARE)
                        .level(30)
                        .inventoryType(InventoryType.CHEST)
                        .icon("url4")
                        .isStackable(false)
                        .build()
        );

        itemCommandRepository.saveAll(items);
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("이름에 '가죽'을 포함하는 아이템을 검색하면 3개의 결과가 반환된다")
        void shouldFindAllItemsWithKoreanKeywordLeather() {
            ItemSearchCondition command = new ItemSearchCondition(null, null, "가죽");
            List<Item> results = itemQueryRepository.findByCondition(command);
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
            ItemSearchCondition command = new ItemSearchCondition(null, null, "덫사냥꾼");
            List<Item> results = itemQueryRepository.findByCondition(command);
            assertThat(results)
                    .hasSize(2)
                    .allSatisfy(item ->
                            assertThat(item.getName().get(LocaleCode.KO_KR.getValue())).contains("덫사냥꾼")
                    );
        }

        @Test
        @DisplayName("이름에 'trapper'를 포함하는 아이템 2개가 검색된다")
        void shouldFindTwoItemsWithKeywordTrapperInEnglish() {
            ItemSearchCondition command = new ItemSearchCondition(null, null, "trapper");
            List<Item> results = itemQueryRepository.findByCondition(command);
            assertThat(results)
                    .hasSize(2)
                    .allSatisfy(item ->
                            assertThat(item.getName().get(LocaleCode.EN_US.getValue()).toLowerCase()).contains("trapper")
                    );
        }

        @Test
        @DisplayName("itemClassId만 지정하면 해당 class의 아이템만 반환")
        void itemClassIdOnly_returnsMatchingItems() {
            ItemSearchCondition cond = new ItemSearchCondition(1L, null, null);
            List<Item> results = itemQueryRepository.findByCondition(cond);
            assertThat(results).hasSize(4);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("존재하지 않는 classId, itemSubclassId는 빈 리스트 반환")
        void notExistClassOrSubclass_returnsEmpty() {
            ItemSearchCondition cond1 = new ItemSearchCondition(999L, null, null);
            ItemSearchCondition cond2 = new ItemSearchCondition(null, 999L, null);
            assertThat(itemQueryRepository.findByCondition(cond1)).isEmpty();
            assertThat(itemQueryRepository.findByCondition(cond2)).isEmpty();
        }
    }

    @Nested
    @DisplayName("실패 케이스(fail)")
    class FailCases {
        @Test
        @DisplayName("이름에 '없는단어'를 검색하면 결과가 비어야 한다")
        void shouldReturnEmptyWhenNameDoesNotMatch() {
            ItemSearchCondition command = new ItemSearchCondition(
                    null, null, "없는단어"
            );
            List<Item> results = itemQueryRepository.findByCondition(command);
            assertThat(results).isEmpty();
        }
    }
}
