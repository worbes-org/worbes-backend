package com.worbes.adapter.blizzard;

import com.worbes.adapter.blizzard.client.BlizzardAccessTokenHandler;
import com.worbes.adapter.blizzard.client.BlizzardApiErrorHandler;
import com.worbes.adapter.blizzard.client.BlizzardApiRestClient;
import com.worbes.adapter.blizzard.client.RestClientErrorHandler;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestClient;

@TestConfiguration
@Import({BlizzardApiErrorHandler.class, BlizzardApiUriFactory.class})
public class BlizzardApiRestClientTestConfig {

    @Bean
    public BlizzardApiRestClient blizzardApiClient(
            RestClient.Builder builder,
            BlizzardAccessTokenHandler tokenHandler,
            RestClientErrorHandler errorHandler
    ) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.initialize();
        return new BlizzardApiRestClient(builder, tokenHandler, errorHandler, threadPoolTaskExecutor);
    }
}
