package com.worbes.infra.blizzard.client;

import com.worbes.infra.blizzard.config.BlizzardApiConfig;
import com.worbes.infra.rest.exception.InternalServerErrorException;
import com.worbes.infra.rest.exception.NotFoundException;
import com.worbes.infra.rest.exception.TooManyRequestsException;
import com.worbes.infra.rest.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BlizzardApiConfig.class})
class BlizzardRetryPolicyTest {

    @Autowired
    RetryTemplate retryTemplate;

    @MockBean
    BlizzardAccessTokenHandler accessTokenHandler;

    BlizzardRetryPolicy policy;

    @BeforeEach
    void setUp() {
        this.policy = new BlizzardRetryPolicy(accessTokenHandler, retryTemplate);
    }

    @Test
    @DisplayName("UnauthorizedException 발생 시 refresh() 후 재시도한다")
    void shouldRefreshTokenAndRetryOnUnauthorized() throws Exception {
        RetryCallback<String, Exception> callback = mock(RetryCallback.class);

        given(callback.doWithRetry(any()))
                .willThrow(new UnauthorizedException())
                .willReturn("result");

        String result = policy.execute(callback);

        then(accessTokenHandler).should().refresh();
        then(callback).should(times(2)).doWithRetry(any());
        assertThat(result).isEqualTo("result");
    }

    @Test
    @DisplayName("TooManyRequestsException 발생 시 최대 3회 재시도")
    void shouldRetryUpToThreeTimesOn429() throws Exception {
        RetryCallback<String, Exception> callback = mock(RetryCallback.class);

        given(callback.doWithRetry(any()))
                .willThrow(new TooManyRequestsException());

        assertThatThrownBy(() -> policy.execute(callback))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(TooManyRequestsException.class);

        then(callback).should(times(3)).doWithRetry(any());
    }

    @Test
    @DisplayName("NotFoundException 발생 시 재시도하지 않고 즉시 실패")
    void shouldNotRetryOnNotFound() throws Exception {
        RetryCallback<String, Exception> callback = mock(RetryCallback.class);

        given(callback.doWithRetry(any()))
                .willThrow(new NotFoundException());

        assertThatThrownBy(() -> policy.execute(callback))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(NotFoundException.class);

        then(callback).should(times(1)).doWithRetry(any());
    }

    @Test
    @DisplayName("기타 예외는 wrapping되어 IllegalStateException으로 던져진다")
    void shouldWrapUnexpectedExceptions() throws Exception {
        RetryCallback<String, Exception> callback = mock(RetryCallback.class);

        given(callback.doWithRetry(any()))
                .willThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> policy.execute(callback))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(RuntimeException.class);

        then(callback).should(times(1)).doWithRetry(any());
    }

    @Test
    @DisplayName("InternalServerErrorException 발생 시 최대 2회 재시도")
    void shouldRetryUpToTwoTimesOnInternalServerError() throws Exception {
        RetryCallback<String, Exception> callback = mock(RetryCallback.class);

        given(callback.doWithRetry(any()))
                .willThrow(new InternalServerErrorException());

        assertThatThrownBy(() -> policy.execute(callback))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(InternalServerErrorException.class);

        then(callback).should(times(2)).doWithRetry(any());
    }

}
