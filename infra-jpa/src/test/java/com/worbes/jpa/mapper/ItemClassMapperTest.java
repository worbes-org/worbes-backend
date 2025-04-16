package com.worbes.jpa.mapper;

import com.worbes.domain.item.ItemClass;
import com.worbes.domain.shared.LocaleCode;
import com.worbes.domain.shared.LocalizedName;
import com.worbes.jpa.entity.ItemClassEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ItemClassMapperTest {
    @Test
    @DisplayName("Entity → Domain 변환 시 LocalizedName 으로 변환된다")
    void entityToDomain() {
        ItemClassEntity entity = new ItemClassEntity(
                17L,
                Map.of("ko_kr", "전투 애완동물", "en_us", "Battle Pets")
        );

        ItemClass domain = ItemClassMapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(17L);
        assertThat(domain.getName().get(LocaleCode.KO_KR)).isEqualTo("전투 애완동물");
        assertThat(domain.getName().get(LocaleCode.EN_US)).isEqualTo("Battle Pets");
    }

    @Test
    @DisplayName("Domain → Entity 변환 시 Map<String, String> 으로 변환된다")
    void domainToEntity() {
        LocalizedName name = LocalizedName.fromRaw(Map.of(
                "ko_kr", "전투 애완동물",
                "en_us", "Battle Pets"
        ));

        ItemClass domain = new ItemClass(17L, name);

        ItemClassEntity entity = ItemClassMapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(17L);
        assertThat(entity.getName()).containsEntry("ko_kr", "전투 애완동물");
        assertThat(entity.getName()).containsEntry("en_us", "Battle Pets");
    }
}
