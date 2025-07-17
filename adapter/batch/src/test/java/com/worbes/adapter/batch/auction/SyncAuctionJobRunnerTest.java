package com.worbes.adapter.batch.auction;

import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetConnectedRealmIdUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;

import java.util.ArrayList;
import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.REALM_ID;
import static com.worbes.adapter.batch.auction.SyncAuctionParameters.REGION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SyncAuctionJobRunnerTest {

    @Mock
    private GetConnectedRealmIdUseCase getConnectedRealmUseCase;

    @Mock
    private Job job;

    @Mock
    private JobLauncher asyncJobLauncher;

    @InjectMocks
    private SyncAuctionJobRunner scheduler;

    @Test
    @DisplayName("realmId 2개와 commodities 에 대해 Job이 각각 실행되어야 한다")
    void runAuctionSyncJob_should_launch_job_for_each_realm_and_commodities() throws Exception {
        // given
        RegionType regionType = RegionType.KR;
        List<Long> realmIds = new ArrayList<>(List.of(101L, 102L));
        given(getConnectedRealmUseCase.execute(regionType)).willReturn(realmIds);

        // when
        scheduler.run();

        // then
        then(asyncJobLauncher).should(times(3)).run(eq(job), any(JobParameters.class));

        ArgumentCaptor<JobParameters> captor = ArgumentCaptor.forClass(JobParameters.class);
        then(asyncJobLauncher).should(times(3)).run(eq(job), captor.capture());

        List<JobParameters> paramsList = captor.getAllValues();

        // realmId 있는 JobParameters 확인
        List<Long> realmIdParams = paramsList.stream()
                .filter(p -> p.getParameters().containsKey(REALM_ID.getKey()))
                .map(p -> p.getLong(REALM_ID.getKey()))
                .toList();

        assertThat(realmIdParams).containsExactlyInAnyOrder(101L, 102L);

        // realmId 없는 JobParameters (commodities)
        long noRealmIdCount = paramsList.stream()
                .filter(p -> !p.getParameters().containsKey(REALM_ID.getKey()))
                .count();

        assertThat(noRealmIdCount).isEqualTo(1);

        // 모든 JobParameters에 region은 KR이어야 함
        assertThat(paramsList).allMatch(p -> regionType.name().equals(p.getString(REGION.getKey())));
    }

    @Test
    @DisplayName("Job 실행 중 예외가 발생하면 RuntimeException으로 래핑되어 던져져야 한다")
    void launchAuctionSyncJob_should_throw_exception_when_job_launcher_fails() throws Exception {
        // given
        willThrow(new JobExecutionAlreadyRunningException("Job already running"))
                .given(asyncJobLauncher).run(any(Job.class), any(JobParameters.class));

        // when & then
        assertThatThrownBy(() -> scheduler.run())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Job already running");
    }
}

