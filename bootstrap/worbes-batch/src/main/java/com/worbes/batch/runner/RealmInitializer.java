package com.worbes.batch.runner;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.InitializeRealmUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealmInitializer {

    private final InitializeRealmUseCase initializeRealmUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void initRealm() {
        initializeRealmUseCase.execute(RegionType.KR);
    }
}
