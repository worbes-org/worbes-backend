package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(FetchAuctionTasklet.class)
@SpringBatchTest
class FetchAuctionTaskletTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 1L;

    @MockBean
    FetchAuctionUseCase fetchAuctionUseCase;

    @Autowired
    Tasklet fetchAuctionTasklet;


    @AfterEach
    void clearMock() {
        clearInvocations(fetchAuctionUseCase);
        reset(fetchAuctionUseCase);
    }

    @SuppressWarnings("unused")
    public StepExecution getStepExecution() {
        return getDefaultStepExecution();
    }

    private StepExecution getStepExecution(JobParameters jobParameters) {
        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    private StepExecution getDefaultStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now())
                .addLong(REALM_ID.getKey(), realmId)
                .toJobParameters();
        return getStepExecution(jobParameters);
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("realmId가 있으면 fetchAuctions가 호출되고 결과가 저장된다")
        void shouldFetchAuctionsAndStoreInExecutionContext() throws Exception {
            List<Auction> expected = List.of(mock(Auction.class), mock(Auction.class));
            given(fetchAuctionUseCase.fetchAuctions(region, realmId)).willReturn(expected);

            StepExecution stepExecution = getDefaultStepExecution();
            StepContribution stepContribution = new StepContribution(stepExecution);
            ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

            RepeatStatus repeatStatus = fetchAuctionTasklet.execute(stepContribution, chunkContext);

            assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
            assertThat(stepExecution.getJobExecution().getExecutionContext().get(AUCTION_SNAPSHOT.getKey())).isEqualTo(expected);
            assertThat(stepExecution.getJobExecution().getExecutionContext().get(AUCTION_COUNT.getKey())).isEqualTo(expected.size());
            then(fetchAuctionUseCase).should(times(1)).fetchAuctions(region, realmId);
        }

        @Test
        @DisplayName("realmId가 null이면 fetchCommodities가 호출된다")
        void shouldFetchCommoditiesWhenRealmIdIsNull() throws Exception {
            List<Auction> expected = List.of(mock(Auction.class));
            given(fetchAuctionUseCase.fetchCommodities(region)).willReturn(expected);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString(REGION.getKey(), region.name())
                    .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now())
                    .toJobParameters();
            StepExecution stepExecution = getStepExecution(jobParameters);
            StepContribution stepContribution = new StepContribution(stepExecution);
            ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

            RepeatStatus repeatStatus = fetchAuctionTasklet.execute(stepContribution, chunkContext);

            assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
            assertThat(stepExecution.getJobExecution().getExecutionContext().get(AUCTION_SNAPSHOT.getKey())).isEqualTo(expected);
            assertThat(stepExecution.getJobExecution().getExecutionContext().get(AUCTION_COUNT.getKey())).isEqualTo(expected.size());
            then(fetchAuctionUseCase).should(times(1)).fetchCommodities(region);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("경매 결과가 비어있으면 예외가 발생한다")
        void shouldThrowWhenResultIsEmpty() {
            given(fetchAuctionUseCase.fetchAuctions(region, realmId)).willReturn(Collections.emptyList());
            StepExecution stepExecution = getDefaultStepExecution();
            StepContribution stepContribution = new StepContribution(stepExecution);
            ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

            assertThatThrownBy(() -> fetchAuctionTasklet.execute(stepContribution, chunkContext))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("경매 스냅샷이 비어 있습니다.");
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("region 파라미터가 없으면 예외 발생")
        void shouldThrowWhenRegionParameterMissing() {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong(REALM_ID.getKey(), realmId)
                    .toJobParameters();
            StepExecution stepExecution = getStepExecution(jobParameters);
            StepContribution stepContribution = new StepContribution(stepExecution);
            ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

            assertThatThrownBy(() -> fetchAuctionTasklet.execute(stepContribution, chunkContext))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Region");
        }

        @Test
        @DisplayName("fetchAuctionUseCase에서 예외 발생 시 예외가 전파된다")
        void shouldPropagateExceptionWhenFetchAuctionFails() {
            given(fetchAuctionUseCase.fetchAuctions(region, realmId)).willThrow(new RuntimeException("fail!"));
            StepExecution stepExecution = getDefaultStepExecution();
            StepContribution stepContribution = new StepContribution(stepExecution);
            ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

            assertThatThrownBy(() -> fetchAuctionTasklet.execute(stepContribution, chunkContext))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("fail!");
        }
    }
}
