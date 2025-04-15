package com.worbes.infra.rest.core.retry;

import com.worbes.infra.rest.core.exception.RestApiClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultRetryExecutorTest {

    @Mock
    private RetryTemplate template;

    @Mock
    private RetryRecoveryStrategy strategy;

    private DefaultRetryExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new DefaultRetryExecutor(template, List.of(strategy));
    }

    @Test
    @DisplayName("전략이 예외를 지원하면 recover가 호출된다")
    void shouldCallRecoverWhenStrategySupportsException() throws Exception {
        // given
        RuntimeException cause = new RuntimeException("예외 발생");

        given(template.execute(any()))
                .willAnswer(invocation -> {
                    RetryCallback<Object, RestApiClientException> callback = invocation.getArgument(0);
                    // 리트라이 전에 예외가 있었던 상황을 시뮬레이션
                    RetryContext mockContext = mock(RetryContext.class);
                    given(mockContext.getLastThrowable()).willReturn(cause);
                    // recovery 실행 검증이 목적이므로 실제 콜백은 생략
                    callback.doWithRetry(mockContext);
                    return null;
                });

        given(strategy.supports(cause)).willReturn(true);

        // when
        executor.execute(context -> "ok");

        // then
        verify(strategy).recover();
    }

    @Test
    @DisplayName("전략이 예외를 지원하지 않으면 recover가 호출되지 않는다")
    void shouldNotCallRecoverWhenStrategyDoesNotSupport() throws Exception {
        RuntimeException cause = new RuntimeException("지원 안하는 예외");

        given(template.execute(any()))
                .willAnswer(invocation -> {
                    RetryContext ctx = mock(RetryContext.class);
                    given(ctx.getLastThrowable()).willReturn(cause);
                    invocation.<RetryCallback<Object, RestApiClientException>>getArgument(0).doWithRetry(ctx);
                    return null;
                });

        given(strategy.supports(cause)).willReturn(false);

        // when
        executor.execute(context -> "ok");

        // then
        verify(strategy, never()).recover();
    }

    @Test
    @DisplayName("콜백 결과를 반환해야 한다")
    void shouldReturnCallbackResult() throws Exception {
        given(template.execute(any()))
                .willAnswer(invocation -> {
                    RetryCallback<String, RestApiClientException> callback = invocation.getArgument(0);
                    RetryContext ctx = mock(RetryContext.class);
                    given(ctx.getLastThrowable()).willReturn(null);
                    return callback.doWithRetry(ctx);
                });

        // when
        String result = executor.execute(context -> "success");

        // then
        assertThat(result).isEqualTo("success");
    }
}
