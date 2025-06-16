package com.worbes.adapter.batch;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.FindConnectedRealmUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.worbes.adapter.batch.SyncAuctionParameters.*;

@Slf4j
@Component
public class SyncAuctionScheduler {

    private final FindConnectedRealmUseCase findConnectedRealmUseCase;
    private final Job job;
    private final JobLauncher asyncJobLauncher;

    public SyncAuctionScheduler(
            FindConnectedRealmUseCase findConnectedRealmUseCase,
            @Qualifier("auctionSyncJob") Job job,
            JobLauncher asyncJobLauncher
    ) {
        this.findConnectedRealmUseCase = findConnectedRealmUseCase;
        this.job = job;
        this.asyncJobLauncher = asyncJobLauncher;
    }

    //    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(cron = "0 0 * * * *")
    public void runAuctionSyncJob() {
        RegionType region = RegionType.KR;
        List<Long> connectedRealmIds = findConnectedRealmUseCase.findConnectedRealmId(region);
        connectedRealmIds.add(null); //for commodities API
        connectedRealmIds.stream()
                .map(id -> createJobParameters(id, region))
                .forEach(this::launchAuctionSyncJob);
    }


    private void launchAuctionSyncJob(JobParameters params) {
        try {
            asyncJobLauncher.run(job, params);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("경매 동기화 Job 실행 실패: realmId={}, {}", params.getLong(REALM_ID.name()), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private JobParameters createJobParameters(Long connectedRealmId, RegionType region) {
        JobParametersBuilder builder = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now());

        // connectedRealmId가 null이 아닐 때만 파라미터에 추가
        if (connectedRealmId != null) {
            builder.addLong(REALM_ID.getKey(), connectedRealmId);
        }

        return builder.toJobParameters();
    }
}
