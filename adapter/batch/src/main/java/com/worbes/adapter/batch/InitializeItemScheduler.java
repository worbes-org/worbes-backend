package com.worbes.adapter.batch;

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

import static com.worbes.adapter.batch.SyncAuctionParameters.AUCTION_DATE;

@Slf4j
@Component
public class InitializeItemScheduler {

    private final Job job;
    private final JobLauncher jobLauncher;

    public InitializeItemScheduler(
            @Qualifier("initializeItemJob") Job job,
            JobLauncher jobLauncher
    ) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    //    @EventListener(ApplicationReadyEvent.class)
    public void launchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
    }
}
