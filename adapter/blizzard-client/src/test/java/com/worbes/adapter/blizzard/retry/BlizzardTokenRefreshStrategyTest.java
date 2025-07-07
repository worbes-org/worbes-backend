package com.worbes.adapter.blizzard.retry;

import com.worbes.adapter.blizzard.client.BlizzardAccessTokenHandlerImpl;
import com.worbes.adapter.blizzard.client.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BlizzardTokenRefreshStrategyTest {

    @Mock
    private BlizzardAccessTokenHandlerImpl oauthClient;

    private BlizzardTokenRefreshStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BlizzardTokenRefreshStrategy(oauthClient);
    }

    @Test
    @DisplayName("UnauthorizedException 이면 supports 는 true 를 반환한다")
    void shouldSupportUnauthorizedException() {
        // given
        Throwable ex = new UnauthorizedException("401");

        // when
        boolean supported = strategy.supports(ex);

        // then
        assertThat(supported).isTrue();
    }

    @Test
    @DisplayName("UnauthorizedException 이 아니면 supports 는 false 를 반환한다")
    void shouldNotSupportOtherExceptions() {
        // given
        Throwable ex = new RuntimeException("some other error");

        // when
        boolean supported = strategy.supports(ex);

        // then
        assertThat(supported).isFalse();
    }

    @Test
    @DisplayName("recover() 가 호출되면 handler.refresh() 도 호출된다")
    void shouldCallHandlerRefreshOnRecover() {
        // when
        strategy.recover();

        // then
        then(oauthClient).should().refresh();
    }
}

