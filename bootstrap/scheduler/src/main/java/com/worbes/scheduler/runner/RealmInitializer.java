package com.worbes.scheduler.runner;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.InitializeRealmUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RealmInitializer implements CommandLineRunner {

    private final InitializeRealmUseCase initializeRealmUseCase;

    @Override
    public void run(String... args) throws Exception {
        initializeRealmUseCase.initialize(RegionType.KR);
    }
}
