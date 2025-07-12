package com.worbes.application.item.service;

import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.FetchItemApiUseCase;
import com.worbes.application.item.port.out.ItemApiFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemApiFetchService implements FetchItemApiUseCase {

    private final ItemApiFetcher itemApiFetcher;

    @Override
    public List<Item> fetchItemAsync(Set<Long> itemIds) throws InterruptedException, ExecutionException, TimeoutException {
        List<CompletableFuture<Item>> futures = itemIds.stream()
                .map(this::fetchItemAsync)
                .toList();

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(30, TimeUnit.SECONDS);

            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            futures.forEach(future -> future.cancel(true));
            throw e;
        }
    }

    private CompletableFuture<Item> fetchItemAsync(Long itemId) {
        return itemApiFetcher.fetchItemAsync(itemId)
                .thenCombine(
                        itemApiFetcher.fetchMediaAsync(itemId),
                        (item, icon) -> {
                            item.setIcon(icon);
                            return item;
                        }
                )
                .exceptionally(this::handleException);
    }

    private Item handleException(Throwable throwable) {
        Throwable cause = unwrapException(throwable);

        if (cause instanceof ItemApiFetchException ife) {
            log.warn("ItemFetchException | itemId={} | status={} | 이유={} | fallback=null",
                    ife.getItemId(), ife.getStatusCode(), ife.getMessage());
            // TODO: 실패한 아이템 저장
            return null;
        }
        log.error("아이템 조회 실패 | cause={}", cause.getClass().getSimpleName(), cause);
        throw new RuntimeException(cause);
    }

    private Throwable unwrapException(Throwable throwable) {
        while (throwable instanceof CompletionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
