package com.worbes.domain.item;

import com.worbes.domain.item.policy.RequiredItemClassPolicy;
import com.worbes.domain.item.port.ItemClassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ItemClassServiceTest {

    @Mock
    RequiredItemClassPolicy policy;

    @Mock
    ItemClassRepository repository;

    @InjectMocks
    ItemClassService service;

    @Test
    @DisplayName("모든 필요한 아이템 클래스가 존재하면 true를 반환한다")
    void allRequiredClassesExist_returnsTrue_whenAllPresent() {
        // given
        Set<Long> requiredIds = Set.of(1L, 2L, 3L);
        given(policy.getRequiredIds()).willReturn(requiredIds);
        given(repository.findAllBy(requiredIds)).willReturn(List.of(
                ItemClass.create(1L, Map.of()),
                ItemClass.create(2L, Map.of()),
                ItemClass.create(3L, Map.of())
        ));

        // when
        boolean result = service.allRequiredClassesExist();

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("일부 아이템 클래스가 누락되면 false를 반환한다")
    void allRequiredClassesExist_returnsFalse_whenSomeMissing() {
        // given
        Set<Long> requiredIds = Set.of(1L, 2L, 3L);
        given(policy.getRequiredIds()).willReturn(requiredIds);
        given(repository.findAllBy(requiredIds)).willReturn(List.of(
                ItemClass.create(1L, Map.of()),
                ItemClass.create(2L, Map.of()) // 3L 누락
        ));

        // when
        boolean result = service.allRequiredClassesExist();

        // then
        assertThat(result).isFalse();
    }
}
