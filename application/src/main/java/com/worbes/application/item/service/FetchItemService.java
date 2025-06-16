package com.worbes.application.item.service;

import com.worbes.application.item.exception.ItemFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.FetchItemUseCase;
import com.worbes.application.item.port.out.ItemFetcher;
import com.worbes.application.item.port.out.MediaFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FetchItemService implements FetchItemUseCase {

    private final ItemFetcher itemFetcher;
    private final ItemFactory itemFactory;
    private final MediaFetcher mediaFetcher;

    @Override
    public List<Item> fetchItemAsync(Set<Long> itemIds) throws InterruptedException {
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
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Item> fetchItemAsync(Long itemId) {
        return itemFetcher.fetchItemAsync(itemId)
                .thenCombine(mediaFetcher.fetchMediaAsync(itemId), itemFactory::create)
                .exceptionally(this::handleException);
    }

    private Item handleException(Throwable throwable) {
        Throwable cause = unwrapException(throwable);

        if (cause instanceof ItemFetchException ife) {
            log.warn("⚠️ 아이템 조회 실패 | itemId={} | status={} | 이유={} | fallback=null",
                    ife.getItemId(), ife.getStatusCode(), ife.getMessage());
            return null; // 복구 가능한 실패
        }

        log.error("💥 예상치 못한 예외 | cause={}", cause.getClass().getSimpleName(), cause);
        throw new RuntimeException("Unexpected error for item " + cause);
    }

    private Throwable unwrapException(Throwable throwable) {
        while (throwable instanceof CompletionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    private List<Item> collectCompletedResults(List<CompletableFuture<Item>> futures) {
        List<Item> results = new ArrayList<>();

        futures.forEach(future -> {
            if (future.isDone()) {
                Item item = future.join();
                if (item != null) {
                    results.add(item);
                }
            } else {
                future.cancel(true);
            }
        });

        return results;
    }
}
