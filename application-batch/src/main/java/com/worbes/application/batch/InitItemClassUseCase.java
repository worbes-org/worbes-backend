package com.worbes.application.batch;

import com.worbes.domain.item.port.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InitItemClassUseCase implements DataInitializeUseCase {

    private final FetchItemClassPort fetcher;
    private final ItemClassRepository repository;

    @Override
    public void init() {
    }
}
