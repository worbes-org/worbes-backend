package com.worbes.adapter.jpa.mapper;

import com.worbes.adapter.jpa.entity.ItemEntity;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Unit::ItemEntityMapper")
class ItemEntityMapperTest {

    private final ItemEntityMapper mapper = Mappers.getMapper(ItemEntityMapper.class);

    @Test
    void toDomain_매핑이_정상적으로_수행된다() {
        // given
        ItemEntity entity = ItemEntity.builder()
                .id(1L)
                .name(Map.of(LocaleCode.KO_KR.getValue(), "검", LocaleCode.EN_US.getValue(), "Sword"))
                .itemClassId(2L)
                .itemSubclassId(3L)
                .quality(QualityType.RARE)
                .level(50)
                .inventoryType(InventoryType.WEAPONMAINHAND)
                .previewItem(Map.of("mock", "data"))
                .iconUrl("http://image.url/icon.png")
                .build();

        // when
        Item item = mapper.toDomain(entity);

        // then
        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).containsEntry(LocaleCode.KO_KR.getValue(), "검");
        assertThat(item.getItemClassId()).isEqualTo(2L);
        assertThat(item.getItemSubclassId()).isEqualTo(3L);
        assertThat(item.getQuality()).isEqualTo(QualityType.RARE);
        assertThat(item.getLevel()).isEqualTo(50);
        assertThat(item.getInventoryType()).isEqualTo(InventoryType.WEAPONMAINHAND);
        assertThat(item.getPreviewItem()).isEqualTo(Map.of("mock", "data"));
        assertThat(item.getIconUrl()).isEqualTo("http://image.url/icon.png");
    }

    @Test
    void toEntity_매핑이_정상적으로_수행된다() {
        // given
        Item item = Item.builder()
                .id(10L)
                .name(Map.of(LocaleCode.EN_US.getValue(), "Axe"))
                .itemClassId(5L)
                .itemSubclassId(6L)
                .quality(QualityType.EPIC)
                .level(70)
                .inventoryType(InventoryType.CHEST)
                .previewItem(Map.of("model", "v1"))
                .iconUrl("http://icon.url/axe.png")
                .build();

        // when
        ItemEntity entity = mapper.toEntity(item);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getName()).containsEntry(LocaleCode.EN_US.getValue(), "Axe");
        assertThat(entity.getItemClassId()).isEqualTo(5L);
        assertThat(entity.getItemSubclassId()).isEqualTo(6L);
        assertThat(entity.getQuality()).isEqualTo(QualityType.EPIC);
        assertThat(entity.getLevel()).isEqualTo(70);
        assertThat(entity.getInventoryType()).isEqualTo(InventoryType.CHEST);
        assertThat(entity.getPreviewItem()).isEqualTo(Map.of("model", "v1"));
        assertThat(entity.getIconUrl()).isEqualTo("http://icon.url/axe.png");
    }
}
