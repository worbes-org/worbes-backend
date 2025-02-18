package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worbes.auctionhousetracker.utils.TestUtils.createDummyItemClassesIndexResponse;
import static com.worbes.auctionhousetracker.utils.TestUtils.createDummyLanguage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ItemClassServiceTest {

    @Mock
    private ItemClassRepository itemClassRepository;

    @Mock
    private RestApiClient restApiClient;

    @InjectMocks
    private ItemClassService itemClassService;

    @Test
    @DisplayName("count() 메서드는 아이템 클래스의 개수를 반환한다")
    void count_ShouldReturnItemClassCount() {
        // Given
        long expectedCount = 5L;
        given(itemClassRepository.count()).willReturn(expectedCount);

        // When
        long result = itemClassService.count();

        // Then
        assertThat(result).isEqualTo(expectedCount);
        verify(itemClassRepository, times(1)).count();
    }

    @Test
    @DisplayName("get(Long id) 메서드는 주어진 ID에 해당하는 아이템 클래스를 반환한다")
    void get_ShouldReturnItemClass_WhenIdExists() {
        // Given
        Long id = 1L;
        ItemClass expectedItemClass = new ItemClass(id, createDummyLanguage());
        given(itemClassRepository.findById(id)).willReturn(Optional.of(expectedItemClass));

        // When
        ItemClass result = itemClassService.get(id);

        // Then
        assertThat(result).isEqualTo(expectedItemClass);
        verify(itemClassRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getAll() 메서드는 모든 아이템 클래스를 반환한다")
    void getAll_ShouldReturnAllItemClasses() {
        // Given
        List<ItemClass> expectedItemClasses = List.of(
                new ItemClass(1L, createDummyLanguage()),
                new ItemClass(2L, createDummyLanguage())
        );
        given(itemClassRepository.findAll()).willReturn(expectedItemClasses);

        // When
        List<ItemClass> result = itemClassService.getAll();

        // Then
        assertThat(result).isEqualTo(expectedItemClasses);
        verify(itemClassRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("save(ItemClass itemClass) 메서드는 아이템 클래스를 저장한다")
    void save_ShouldSaveItemClass() {
        // Given
        ItemClass itemClass = new ItemClass(1L, createDummyLanguage());

        // When
        itemClassService.save(itemClass);

        // Then
        verify(itemClassRepository, times(1)).save(itemClass);
    }

    @Test
    @DisplayName("fetchItemClassesIndex() 메서드는 API에서 아이템 클래스를 가져온다")
    void fetchItemClassesIndex_ShouldFetchItemClassesFromApi() {
        // Given
        ItemClassesIndexResponse response = createDummyItemClassesIndexResponse();
        given(restApiClient.get(anyString(), Map.of("namespace", "static-kr"), eq(ItemClassesIndexResponse.class))).willReturn(response);

        // When
        List<ItemClass> result = itemClassService.fetchItemClassesIndex();

        // Then
        assertThat(result).hasSize(response.getItemClasses().size());
        verify(restApiClient, times(1)).get(anyString(), Map.of("namespace", "static-kr"), eq(ItemClassesIndexResponse.class));
    }
}
