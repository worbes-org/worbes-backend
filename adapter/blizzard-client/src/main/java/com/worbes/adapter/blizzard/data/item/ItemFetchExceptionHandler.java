package com.worbes.adapter.blizzard.data.item;

import com.worbes.adapter.blizzard.client.BlizzardApiException;
import com.worbes.application.item.exception.ItemApiFetchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class ItemFetchExceptionHandler {

    public <T> Function<Throwable, T> handle(String fetcherName, Long itemId) {
        return throwable -> {
            if (throwable instanceof BlizzardApiException e) {
                log.error("❌ [{}] API 호출 실패 | itemId={} | status={} | message={} | cause={}",
                        fetcherName, itemId, e.getStatusCode(), e.getMessage(), e.getClass().getSimpleName(), e);
                throw new ItemApiFetchException(fetcherName + " API 조회 중 예외", e, e.getStatusCode(), itemId);
            }
            log.error("❌ [{}] 알 수 없는 예외 발생 | itemId={} | message={} | cause={}",
                    fetcherName, itemId, throwable.getMessage(), throwable.getClass().getSimpleName(), throwable);
            throw new ItemApiFetchException(throwable.getMessage(), throwable.getCause(), itemId);
        };
    }
}
