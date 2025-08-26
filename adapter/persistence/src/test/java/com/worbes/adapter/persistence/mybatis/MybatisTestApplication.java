package com.worbes.adapter.persistence.mybatis;

import com.worbes.adapter.persistence.PersistenceConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.worbes.adapter.persistence.mybatis"})
@Import(PersistenceConfig.class)
public class MybatisTestApplication {
}
