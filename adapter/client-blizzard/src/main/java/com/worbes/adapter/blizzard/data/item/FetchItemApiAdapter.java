package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.client.BlizzardApiException;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.port.out.FetchItemApiPort;
import com.worbes.application.item.port.out.FetchItemApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FetchItemApiAdapter implements FetchItemApiPort {

    private final BlizzardApiClient apiClient;
    private final ItemResponseMapper responseMapper;
    private final BlizzardApiUriFactory uriFactory;
    private final BlizzardResponseValidator validator;

    @Override
    public CompletableFuture<FetchItemApiResult> fetchAsync(Long id) {
        URI uri = uriFactory.itemUri(id);

        return apiClient.fetchAsync(uri, ItemResponse.class)
                .thenApply(responseMapper::toDomain)
                .thenApply(validator::validate)
                .exceptionally(throwable -> {
                    if (throwable instanceof BlizzardApiException e) {
                        log.error("❌ Item API 호출 실패 | itemId={} | status={} | message={} | cause={}",
                                id, e.getStatusCode(), e.getMessage(), e.getClass().getSimpleName(), e);
                        throw new ItemApiFetchException("Item API 조회 중 예외", e, e.getStatusCode(), id);
                    }
                    log.error("❌ Item API 알 수 없는 예외 발생 | itemId={} | message={} | cause={}",
                            id, throwable.getMessage(), throwable.getClass().getSimpleName(), throwable);
                    throw new ItemApiFetchException(throwable.getMessage(), throwable.getCause(), id);
                });
    }
}
