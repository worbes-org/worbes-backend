package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.client.BlizzardApiException;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.FetchItemApiPort;
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
    private final BlizzardApiUriFactory uriFactory;
    private final ItemJsonLoader itemJsonLoader;
    private final BlizzardResponseValidator validator;

    @Override
    public CompletableFuture<Item> fetchAsync(Long id) {
        URI uri = uriFactory.itemUri(id);

        return apiClient.fetchAsync(uri, ItemBlizzardApiResponse.class)
                .thenApply(validator::validate)
                .thenCombine(itemJsonLoader.fetchAsync(id), this::convertToDomain)
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

    private Item convertToDomain(ItemBlizzardApiResponse blizzard, ItemWowHeadApiResponse wowHead) {
        return Item.builder()
                .id(blizzard.id())
                .name(blizzard.name())
                .level(blizzard.level())
                .classId(blizzard.classId())
                .subclassId(blizzard.subclassId())
                .quality(blizzard.qualityType())
                .isStackable(blizzard.isStackable())
                .inventoryType(blizzard.inventoryTypeValue())
                .icon(wowHead.icon())
                .expansionId(wowHead.expansion())
                .craftingTier(wowHead.craftingQualityTier())
                .displayId(wowHead.display())
                .build();
    }
}
