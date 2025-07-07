package com.worbes.adapter.blizzard.retry;

import com.worbes.adapter.blizzard.client.BlizzardAccessTokenHandler;
import com.worbes.adapter.blizzard.client.InternalServerErrorException;
import com.worbes.adapter.blizzard.client.TooManyRequestsException;
import com.worbes.adapter.blizzard.client.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.RetryCallback;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultRetryExecutorSpringBootTest {

    private static final String SUCCESS = "success";
    @Autowired
    private RetryExecutor retryExecutor;
    @MockBean
    private BlizzardAccessTokenHandler accessTokenHandler;

    @Test
    @DisplayName("UnauthorizedException 발생 시 refresh() 호출 후 2회 재시도 후 성공")
    void shouldRetryAndRecoverOnUnauthorizedException() {
        // given
        AtomicInteger attempts = new AtomicInteger(0);

        RetryCallback<String, UnauthorizedException> callback = context -> {
            if (attempts.getAndIncrement() < 1) {
                throw new UnauthorizedException("Unauthorized");
            }
            return SUCCESS;
        };

        // when
        String result = retryExecutor.execute(callback);

        // then
        assertThat(result).isEqualTo(SUCCESS);
        assertThat(attempts.get()).isEqualTo(2); // 최초 1회 실패 + 1회 재시도
        verify(accessTokenHandler, times(1)).refresh();
    }

    @Test
    @DisplayName("UnauthorizedException: 최대 2회 재시도 후 실패 시 예외 발생 & refresh 호출")
    void shouldThrowAfterUnauthorizedMaxRetry() {
        // given
        AtomicInteger attempts = new AtomicInteger(0);

        RetryCallback<String, UnauthorizedException> callback = context -> {
            attempts.incrementAndGet();
            throw new UnauthorizedException("Unauthorized");
        };

        // when / then
        assertThatThrownBy(() -> retryExecutor.execute(callback))
                .isInstanceOf(UnauthorizedException.class);

        assertThat(attempts.get()).isEqualTo(2); // 최대 2번 시도됨
        verify(accessTokenHandler, times(1)).refresh();
    }

    @Test
    @DisplayName("InternalServerErrorException 발생 시 최대 2회 재시도 후 실패")
    void shouldRetryUpToTwoTimesOnInternalServerError() {
        // given
        AtomicInteger attempts = new AtomicInteger(0);

        RetryCallback<String, InternalServerErrorException> callback = context -> {
            attempts.incrementAndGet();
            throw new InternalServerErrorException("500");
        };

        // when / then
        assertThatThrownBy(() -> retryExecutor.execute(callback))
                .isInstanceOf(InternalServerErrorException.class);
        assertThat(attempts.get()).isEqualTo(2); // 최대 2회
        verify(accessTokenHandler, never()).refresh(); // 이 예외는 recover 안 함
    }

    @Test
    @DisplayName("TooManyRequestsException 발생 시 최대 3회 재시도 후 실패")
    void shouldRetryUpToThreeTimesOnTooManyRequestsException() {
        // given
        AtomicInteger attempts = new AtomicInteger(0);

        RetryCallback<String, TooManyRequestsException> callback = context -> {
            attempts.incrementAndGet();
            throw new TooManyRequestsException("429");
        };

        // when / then
        assertThatThrownBy(() -> retryExecutor.execute(callback))
                .isInstanceOf(TooManyRequestsException.class);
        assertThat(attempts.get()).isEqualTo(3); // 최대 3회
        verify(accessTokenHandler, never()).refresh(); // 이 예외도 recover 안 함
    }
}
