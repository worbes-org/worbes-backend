package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.UnauthorizedException;
import com.worbes.application.item.exception.ItemApiFetchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemApiFetchExceptionHandlerTest {

    private final ItemFetchExceptionHandler handler = new ItemFetchExceptionHandler();

    @Test
    @DisplayName("BlizzardApiException 발생 시 ItemFetchException으로 변환한다")
    void should_wrap_BlizzardApiException_into_ItemFetchException() {
        // given
        Long itemId = 101L;
        String fetcherName = "Item";

        UnauthorizedException cause = new UnauthorizedException();

        // when
        Function<Throwable, Object> exceptionFunction = handler.handle(fetcherName, itemId);

        // then
        assertThatThrownBy(() -> exceptionFunction.apply(cause))
                .isInstanceOf(ItemApiFetchException.class)
                .satisfies(e -> {
                    ItemApiFetchException ex = (ItemApiFetchException) e;
                    assertThat(ex.getItemId()).isEqualTo(itemId);
                    assertThat(ex.getStatusCode()).isEqualTo(cause.getStatusCode());
                    assertThat(ex.getMessage()).contains("Item API 조회 중 예외");
                });
    }

    @Test
    @DisplayName("알 수 없는 예외 발생 시 ItemFetchException 던진다")
    void should_wrap_unknown_exception_into_CompletionException() {
        // given
        Long itemId = 202L;
        String fetcherName = "Media";
        RuntimeException cause = new RuntimeException("Unexpected error");

        // when
        Function<Throwable, Object> exceptionFunction = handler.handle(fetcherName, itemId);

        // then
        assertThatThrownBy(() -> exceptionFunction.apply(cause))
                .isInstanceOf(ItemApiFetchException.class);
    }

    @Test
    @DisplayName("예외에 cause가 있을 경우 ItemFetchException은 cause를 내부에 포함한다")
    void should_use_inner_cause_if_present_in_unknown_exception() {
        // given
        Long itemId = 303L;
        String fetcherName = "Media";
        Throwable rootCause = new IllegalArgumentException("invalid");
        Throwable wrapped = new RuntimeException("wrapper", rootCause);

        // when
        Function<Throwable, Object> exceptionFunction = handler.handle(fetcherName, itemId);

        // then
        assertThatThrownBy(() -> exceptionFunction.apply(wrapped))
                .isInstanceOf(ItemApiFetchException.class)
                .hasCause(rootCause);
    }
}
