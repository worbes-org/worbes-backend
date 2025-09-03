package com.worbes.adapter.persistence.jpa;

import com.worbes.adapter.persistence.PersistenceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.worbes.adapter.persistence.jpa"})
@Import(PersistenceConfig.class)
@Slf4j
public class JpaTestApplication {
}
