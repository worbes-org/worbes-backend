package com.worbes.adapter.batch.auction;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetConnectedRealmIdUseCase;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.*;

@Slf4j
@Component
public class SyncAuctionScheduler {

    private final GetConnectedRealmIdUseCase getConnectedRealmUseCase;
    private final Job job;
    private final JobLauncher asyncJobLauncher;

    public SyncAuctionScheduler(
            GetConnectedRealmIdUseCase getConnectedRealmUseCase,
            @Qualifier("auctionSyncJob") Job job,
            JobLauncher asyncJobLauncher
    ) {
        this.getConnectedRealmUseCase = getConnectedRealmUseCase;
        this.job = job;
        this.asyncJobLauncher = asyncJobLauncher;
    }

    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(cron = "0 0 * * * *")
    public void runAuctionSyncJob() {
        RegionType region = RegionType.KR;
        launchCommoditySyncJob(region);
        launchAuctionSyncJob(region);
    }

    private void runJobLauncher(JobParameters params) {
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

    private JobParameters createJobParameters(RegionType region, Long connectedRealmId) {
        JobParametersBuilder builder = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now());

        if (connectedRealmId == null) {
            return builder.toJobParameters();
        }
        builder.addLong(REALM_ID.getKey(), connectedRealmId);

        return builder.toJobParameters();
    }

    private void launchCommoditySyncJob(RegionType region) {
        runJobLauncher(createJobParameters(region, null));
    }

    private void launchAuctionSyncJob(RegionType region) {
        List<Long> connectedRealmIds = getConnectedRealmUseCase.getAllConnectedRealmId(region);
        connectedRealmIds.stream()
                .map(id -> createJobParameters(region, id))
                .forEach(this::runJobLauncher);
    }
}
