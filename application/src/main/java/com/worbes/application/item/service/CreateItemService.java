package com.worbes.application.item.service;

import com.worbes.application.item.exception.ItemApiFetchException;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.in.CreateItemUseCase;
import com.worbes.application.item.port.out.FetchExtraItemInfoPort;
import com.worbes.application.item.port.out.FetchItemApiPort;
import com.worbes.application.item.port.out.SaveItemPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@Profile("batch")
@RequiredArgsConstructor
public class CreateItemService implements CreateItemUseCase {

    private final FetchItemApiPort fetchItemApiPort;
    private final FetchExtraItemInfoPort fetchExtraItemInfoPort;
    private final SaveItemPort saveItemPort;

    public void execute(Set<Long> itemIds) {
        List<CompletableFuture<Item>> futures = new ArrayList<>();
        for (Long itemId : itemIds) {
            CompletableFuture<Item> future = fetchItemApiPort.fetchAsync(itemId)
                    .thenCombine(fetchExtraItemInfoPort.fetchAsync(itemId), Item::from)
                    .exceptionally(this::handleException);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<Item> items = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
        saveItemPort.saveAll(items);
        log.info("아이템 저장 완료 | 저장된 개수: {}", items.size());
    }

    private Item handleException(Throwable throwable) {
        Throwable cause = unwrapException(throwable);
        if (cause instanceof ItemApiFetchException ife) {
            log.warn("[아이템 조회 실패] fallback 적용 | itemId={} | status={} | message={}",
                    ife.getItemId(), ife.getStatusCode(), ife.getMessage());
            // TODO: 실패한 아이템 저장
            return null;
        }
        log.error("[아이템 조회 처리 중 알 수 없는 예외 발생] fallback 없음 | 원인: {} | 전체 스택: ",
                cause.getClass().getSimpleName(), cause);
        throw new RuntimeException(cause);
    }

    private Throwable unwrapException(Throwable throwable) {
        while (throwable instanceof CompletionException && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }
}
