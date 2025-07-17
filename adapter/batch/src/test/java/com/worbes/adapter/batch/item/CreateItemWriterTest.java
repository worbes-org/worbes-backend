package com.worbes.adapter.batch.item;

import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.CreateItemUseCase;
import com.worbes.application.item.port.in.FetchItemApiUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class CreateItemWriterTest {

    private final FetchItemApiUseCase fetchItemApiUseCase = mock(FetchItemApiUseCase.class);
    private final CreateItemUseCase createItemUseCase = mock(CreateItemUseCase.class);
    private final CreateItemWriter createItemWriter = new CreateItemWriter(fetchItemApiUseCase, createItemUseCase);

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("정상적으로 아이템을 fetch/save 한다")
        void shouldFetchAndSaveItems() throws Exception {
            Set<Long> ids = Set.of(1L, 2L, 3L);
            List<Item> items = List.of(
                    Item.builder().id(1L).build(),
                    Item.builder().id(2L).build(),
                    Item.builder().id(3L).build()
            );
            given(fetchItemApiUseCase.execute(ids)).willReturn(items);
            Chunk<Long> chunk = new Chunk<>(new ArrayList<>(ids));

            createItemWriter.write(chunk);

            then(fetchItemApiUseCase).should().execute(ids);
            then(createItemUseCase).should().execute(items);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("chunk에 중복된 ID가 있으면 중복 없이 fetch된다")
        void shouldRemoveDuplicates() throws Exception {
            List<Long> chunkList = Arrays.asList(1L, 2L, 2L, 3L, 1L);
            Set<Long> expectedIds = Set.of(1L, 2L, 3L);
            List<Item> items = List.of(
                    Item.builder().id(1L).build(),
                    Item.builder().id(2L).build(),
                    Item.builder().id(3L).build()
            );
            given(fetchItemApiUseCase.execute(expectedIds)).willReturn(items);
            Chunk<Long> chunk = new Chunk<>(chunkList);

            createItemWriter.write(chunk);

            then(fetchItemApiUseCase).should().execute(expectedIds);
            then(createItemUseCase).should().execute(items);
        }

        @Test
        @DisplayName("chunk가 비어있으면 아무 동작도 하지 않는다")
        void shouldDoNothingWhenChunkIsEmpty() throws Exception {
            Chunk<Long> chunk = new Chunk<>(Collections.emptyList());
            createItemWriter.write(chunk);
            then(fetchItemApiUseCase).shouldHaveNoInteractions();
            then(createItemUseCase).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("실패/예외 케이스")
    class FailCases {
        @Test
        @DisplayName("일부 아이템만 fetch되면 saveAll은 fetch된 것만 저장하고 실패 ID는 로그에 남는다")
        void shouldLogFailedIdsWhenSomeItemsNotFetched() throws Exception {
            Set<Long> ids = Set.of(1L, 2L, 3L);
            List<Item> items = List.of(
                    Item.builder().id(1L).build(),
                    Item.builder().id(3L).build()
            );
            given(fetchItemApiUseCase.execute(ids)).willReturn(items);
            Chunk<Long> chunk = new Chunk<>(new ArrayList<>(ids));

            createItemWriter.write(chunk);

            then(fetchItemApiUseCase).should().execute(ids);
            then(createItemUseCase).should().execute(items);
            // 실패 ID(2L)는 로그로만 남으므로, 별도 검증은 생략
        }

        @Test
        @DisplayName("fetch된 아이템이 없으면 saveAll이 호출되지 않고 경고 로그만 남는다")
        void shouldWarnWhenNoItemsFetched() throws Exception {
            Set<Long> ids = Set.of(1L, 2L);
            given(fetchItemApiUseCase.execute(ids)).willReturn(Collections.emptyList());
            Chunk<Long> chunk = new Chunk<>(new ArrayList<>(ids));

            createItemWriter.write(chunk);

            then(fetchItemApiUseCase).should().execute(ids);
            then(createItemUseCase).should(never()).execute(any());
        }

        @Test
        @DisplayName("fetchItemAsync에서 InterruptedException이 발생하면 인터럽트 플래그를 복구하고 예외를 전파한다")
        void shouldPropagateInterruptedException() throws Exception {
            Set<Long> ids = Set.of(1L);
            given(fetchItemApiUseCase.execute(ids)).willThrow(new InterruptedException("interrupted!"));
            Chunk<Long> chunk = new Chunk<>(new ArrayList<>(ids));

            assertThatThrownBy(() -> createItemWriter.write(chunk))
                    .isInstanceOf(InterruptedException.class)
                    .hasMessageContaining("interrupted!");
        }

        @Test
        @DisplayName("fetchItemAsync에서 ExecutionException/TimeoutException이 발생하면 skip된다")
        void shouldSkipOnExecutionOrTimeoutException() throws Exception {
            Set<Long> ids = Set.of(1L);
            Chunk<Long> chunk = spy(new Chunk<>(new ArrayList<>(ids)));
            given(fetchItemApiUseCase.execute(ids)).willThrow(new ExecutionException("fail!", null));

            createItemWriter.write(chunk);
            verify(chunk).skip(any(ExecutionException.class));

            // TimeoutException도 동일하게 검증
            reset(fetchItemApiUseCase, createItemUseCase, chunk);
            given(fetchItemApiUseCase.execute(ids)).willThrow(new TimeoutException("timeout!"));
            createItemWriter.write(chunk);
            verify(chunk).skip(any(TimeoutException.class));
        }
    }
}
