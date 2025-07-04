package com.worbes.scheduler.runner;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@RequiredArgsConstructor
public class FlywayMigrator implements ApplicationRunner {

    private final Flyway batchFlyway;
    private final Flyway appFlyway;

    @Override
    public void run(ApplicationArguments args) {
        appFlyway.migrate();
        batchFlyway.migrate();
    }
}
