package com.worbes.auctionhousetracker.config;

import com.worbes.auctionhousetracker.config.properties.BlizzardApiConfigProperties;
import com.worbes.auctionhousetracker.config.properties.RequiredItemClassesProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableJpaAuditing
@Slf4j
@EnableConfigurationProperties({
        BlizzardApiConfigProperties.class,
        RequiredItemClassesProperties.class})
public class AppConfig {

    @Bean
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setThreadNamePrefix("AsyncExecutor-");
        return executor;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);  // 스케줄링을 위한 스레드 개수
        scheduler.setThreadNamePrefix("ScheduledTask-");
        scheduler.initialize();
        return scheduler;
    }
}
