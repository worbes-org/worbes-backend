package com.worbes.adapter.jpa.item;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ItemEntityMapperTest {

    private final ItemEntityMapper mapper = Mappers.getMapper(ItemEntityMapper.class);

    @Test
    void testEntityToDomain() {
        // given
        ItemEntity entity = ItemEntity.builder()
                .id(1L)
                .name(Map.of(LocaleCode.KO_KR.getValue(), "검", LocaleCode.EN_US.getValue(), "Sword"))
                .classId(2L)
                .subclassId(3L)
                .quality(QualityType.RARE)
                .level(50)
                .inventoryType(InventoryType.WEAPONMAINHAND)
                .icon("icon")
                .build();

        // when
        Item item = mapper.toDomain(entity);

        // then
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).containsEntry(LocaleCode.KO_KR.getValue(), "검");
        assertThat(item.getClassId()).isEqualTo(2L);
        assertThat(item.getSubclassId()).isEqualTo(3L);
        assertThat(item.getQuality()).isEqualTo(QualityType.RARE);
        assertThat(item.getLevel()).isEqualTo(50);
        assertThat(item.getInventoryType()).isEqualTo(InventoryType.WEAPONMAINHAND);
        assertThat(item.getIcon()).isEqualTo("icon");
    }

    @Test
    void testDomainToEntity() {
        // given
        Item item = Item.builder()
                .id(10L)
                .name(Map.of(LocaleCode.EN_US.getValue(), "Axe"))
                .classId(5L)
                .subclassId(6L)
                .quality(QualityType.EPIC)
                .level(70)
                .inventoryType(InventoryType.CHEST)
                .icon("axe")
                .build();

        // when
        ItemEntity entity = mapper.toEntity(item);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getName()).containsEntry(LocaleCode.EN_US.getValue(), "Axe");
        assertThat(entity.getClassId()).isEqualTo(5L);
        assertThat(entity.getSubclassId()).isEqualTo(6L);
        assertThat(entity.getQuality()).isEqualTo(QualityType.EPIC);
        assertThat(entity.getLevel()).isEqualTo(70);
        assertThat(entity.getInventoryType()).isEqualTo(InventoryType.CHEST);
        assertThat(entity.getIcon()).isEqualTo("axe");
    }
}
