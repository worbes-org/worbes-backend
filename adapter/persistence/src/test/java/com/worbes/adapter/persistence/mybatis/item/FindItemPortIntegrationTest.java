package com.worbes.adapter.persistence.mybatis.item;

import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.SearchItemQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/sql/item/find_item_port_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class FindItemPortIntegrationTest {

    @Autowired
    ItemMybatisAdapter itemMybatisAdapter;

    @Test
    @DisplayName("전체 조건이 정확히 일치하는 아이템만 검색된다")
    void exactMatchWithAllConditions() {
        // given
        SearchItemQuery query = new SearchItemQuery(1L, 1L, "검", 2, 6, 1);

        // when
        List<Item> result = itemMybatisAdapter.findBy(query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("부분 조건(classId만)으로 검색해도 정상 동작한다")
    void partialCondition_classIdOnly() {
        // given
        SearchItemQuery query = new SearchItemQuery(1L, null, null, null, null, null);

        // when
        List<Item> result = itemMybatisAdapter.findBy(query);

        // then
        assertThat(result).extracting("id").containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("이름(ko_KR)으로 검색이 정상 동작한다")
    void nameSearch_koKR() {
        // given
        SearchItemQuery query = new SearchItemQuery(null, null, "활", null, null, null);

        // when
        List<Item> result = itemMybatisAdapter.findBy(query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName(LocaleCode.KO_KR)).isEqualTo("활");
    }

    @Test
    @DisplayName("quality 범위(min~max)에 속하는 아이템만 반환된다")
    void qualityRangeSearch() {
        // given
        SearchItemQuery query = new SearchItemQuery(null, null, null, 2, 3, null);

        // when
        List<Item> result = itemMybatisAdapter.findBy(query);

        // then
        assertThat(result).isNotEmpty(); // 결과가 비어있지 않은지 확인
        assertThat(result)
                .allSatisfy(item ->
                        assertThat(item.getQuality().getValue())
                                .isBetween(2, 3) // minQuality ~ maxQuality 범위 확인
                );
    }

    @Test
    @DisplayName("expansionId로만 검색해도 정상 동작한다")
    void searchByExpansionIdOnly() {
        // given
        SearchItemQuery query = new SearchItemQuery(null, null, null, null, null, 2);

        // when
        List<Item> result = itemMybatisAdapter.findBy(query);

        // then
        assertThat(result).isNotEmpty(); // 결과가 비어있지 않은지 확인
        assertThat(result)
                .allSatisfy(item -> assertThat(item.getExpansionId()).isEqualTo(2));
    }

    @Test
    @DisplayName("여러 조건(classId, name, quality)을 조합해도 동작한다")
    void combinedConditions() {
        // given
        SearchItemQuery query = new SearchItemQuery(2L, null, "활", 4, 6, 2);

        // when
        List<Item> result = itemMybatisAdapter.findBy(query);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
    }
}
