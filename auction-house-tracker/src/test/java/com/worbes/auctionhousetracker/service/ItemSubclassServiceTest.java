package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.ItemClassResponse;
import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.ItemSubclass;
import com.worbes.auctionhousetracker.entity.enums.NamespaceType;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.oauth2.RestApiClient;
import com.worbes.auctionhousetracker.repository.ItemSubclassRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemSubclassServiceTest {

    @Mock
    ItemSubclassRepository itemSubclassRepository;

    @Mock
    RestApiClient restApiClient;

    @InjectMocks
    ItemSubclassService itemSubclassService;

    @Test
    @DisplayName("count()는 서브클래스의 개수를 반환한다")
    void count_ShouldReturnNumberOfSubclasses() {
        // Given
        given(itemSubclassRepository.count()).willReturn(10L);

        // When
        Long result = itemSubclassService.count();

        // Then
        assertThat(result).isEqualTo(10L);
        verify(itemSubclassRepository, times(1)).count(); // Verify that count() method was called once
    }

    // Test for getAll() method
    @Test
    @DisplayName("getAll()은 모든 서브클래스를 반환한다")
    void getAll_ShouldReturnAllSubclasses() {
        // Given
        ItemSubclass mocked1 = mock(ItemSubclass.class);
        ItemSubclass mocked2 = mock(ItemSubclass.class);
        List<ItemSubclass> dummySubclasses = List.of(mocked1, mocked2);
        given(itemSubclassRepository.findAll()).willReturn(dummySubclasses);

        // When
        List<ItemSubclass> result = itemSubclassService.getAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(dummySubclasses);
        verify(itemSubclassRepository, times(1)).findAll(); // Verify findAll() method was called once
    }

    // Test for save() method
    @Test
    @DisplayName("save()는 서브클래스를 저장한다")
    void save_ShouldSaveSubclass() {
        // Given
        ItemSubclass mocked1 = mock(ItemSubclass.class);

        // When
        itemSubclassService.save(mocked1);

        // Then
        verify(itemSubclassRepository, times(1)).save(mocked1); // Verify that save() was called once
    }

    @Test
    @DisplayName("fetchItemSubclassIds()는 서브클래스 ID 목록을 반환한다")
    void fetchItemSubclassIds_ShouldReturnSubclassIds() {
        // Given
        ItemClass itemClass = mock(ItemClass.class);
        ItemClassResponse itemClassResponse = mock(ItemClassResponse.class);
        List<ItemClassResponse.Subclass> subclasses = List.of(
                createSubclass(1L),
                createSubclass(2L)
        );

        given(itemClass.getId()).willReturn(1L);
        given(itemClassResponse.getSubclassResponses()).willReturn(subclasses);
        given(restApiClient.get(anyString(), anyMap(), eq(ItemClassResponse.class)))
                .willReturn(itemClassResponse);

        // When
        List<Long> result = itemSubclassService.fetchItemSubclassIds(itemClass.getId());

        // Then
        assertThat(result).containsExactly(1L, 2L);
        Region region = Region.US;
        verify(restApiClient, times(1))
                .get(
                        eq(BlizzardApiUrlBuilder.builder(region).itemClass(itemClass.getId()).build()),
                        eq(BlizzardApiParamsBuilder.builder(region).namespace(NamespaceType.STATIC).build()),
                        eq(ItemClassResponse.class)
                );
    }

    private ItemClassResponse.Subclass createSubclass(Long id) {
        ItemClassResponse.Subclass subclass = mock(ItemClassResponse.Subclass.class);
        given(subclass.getId()).willReturn(id);
        return subclass;
    }

    // Test for fetchItemSubclass() method
    @Test
    @DisplayName("fetchItemSubclass()는 ItemSubclass를 반환한다")
    void fetchItemSubclass_ShouldReturnItemSubclass() {
        // Given
        Region region = Region.US;
        ItemClass itemClass = mock(ItemClass.class);
        Long subclassId = 2L;
        ItemSubclassResponse itemSubclassResponse = mock(ItemSubclassResponse.class);
        given(itemClass.getId()).willReturn(1L);
        given(restApiClient.get(anyString(), anyMap(), eq(ItemSubclassResponse.class)))
                .willReturn(itemSubclassResponse);

        // When
        ItemSubclass result = itemSubclassService.fetchItemSubclass(itemClass, subclassId);

        // Then
        assertThat(result).isNotNull(); // Verify that a non-null ItemSubclass is returned
        verify(restApiClient, times(1))
                .get(
                        eq(BlizzardApiUrlBuilder.builder(region).itemSubclass(itemClass.getId(), subclassId).build()),
                        eq(BlizzardApiParamsBuilder.builder(region).namespace(NamespaceType.STATIC).build()),
                        eq(ItemSubclassResponse.class)
                ); // Verify fetchData was called once
    }
}
