package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;

import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameter.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class FetchAuctionTaskletTest {
    @Test
    @DisplayName("정상 실행 시 ExecutionContext에 snapshot과 count가 저장된다")
    void execute_validParameters_putsSnapshotAndCountInContext() throws Exception {
        // given
        FetchAuctionUseCase mockUseCase = mock(FetchAuctionUseCase.class);
        FetchAuctionTasklet tasklet = new FetchAuctionTasklet(mockUseCase);

        List<Auction> snapshot = List.of(
                Auction.builder()
                        .id(1L)
                        .itemId(210930L)
                        .quantity(200)
                        .unitPrice(1000L)
                        .region(RegionType.KR)
                        .build(),
                Auction.builder()
                        .id(1L)
                        .itemId(212444L)
                        .quantity(1)
                        .buyout(1000L)
                        .realmId(205L)
                        .region(RegionType.KR)
                        .build()
        );
        given(mockUseCase.execute(RegionType.KR, 123L)).willReturn(snapshot);

        JobExecution jobExecution = new JobExecution(1L,
                new JobParametersBuilder()
                        .addString(REGION.getKey(), "KR")
                        .addLong(REALM_ID.getKey(), 123L)
                        .toJobParameters());

        StepContribution contribution = mock(StepContribution.class);
        ChunkContext chunkContext = new ChunkContext(new StepContext(
                StepSynchronizationManager.register(jobExecution.createStepExecution("step1")).getStepExecution()));

        // when
        tasklet.execute(contribution, chunkContext);

        // then
        assertThat(jobExecution.getExecutionContext().get(AUCTION_SNAPSHOT.getKey()))
                .isEqualTo(snapshot);
        assertThat(jobExecution.getExecutionContext().get(AUCTION_COUNT.getKey()))
                .isEqualTo(snapshot.size());
    }

    @Test
    @DisplayName("snapshot이 비어 있으면 IllegalStateException 발생")
    void execute_emptySnapshot_throwsException() {
        // given
        FetchAuctionUseCase mockUseCase = mock(FetchAuctionUseCase.class);
        FetchAuctionTasklet tasklet = new FetchAuctionTasklet(mockUseCase);

        given(mockUseCase.execute(RegionType.KR, 123L)).willReturn(List.of());

        JobExecution jobExecution = new JobExecution(1L,
                new JobParametersBuilder()
                        .addString(REGION.getKey(), "KR")
                        .addLong(REALM_ID.getKey(), 123L)
                        .toJobParameters());

        StepContribution contribution = mock(StepContribution.class);
        ChunkContext chunkContext = new ChunkContext(new StepContext(
                StepSynchronizationManager.register(jobExecution.createStepExecution("step1")).getStepExecution()));

        // when & then
        assertThatThrownBy(() -> tasklet.execute(contribution, chunkContext))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("경매 스냅샷이 비어 있습니다.");
    }

    @Test
    @DisplayName("Region 파라미터가 없으면 IllegalArgumentException 발생")
    void execute_missingRegion_throwsException() {
        // given
        FetchAuctionUseCase mockUseCase = mock(FetchAuctionUseCase.class);
        FetchAuctionTasklet tasklet = new FetchAuctionTasklet(mockUseCase);

        JobExecution jobExecution = new JobExecution(1L,
                new JobParametersBuilder()
                        .addLong(REALM_ID.getKey(), 123L)
                        .toJobParameters());

        StepContribution contribution = mock(StepContribution.class);
        ChunkContext chunkContext = new ChunkContext(new StepContext(
                StepSynchronizationManager.register(jobExecution.createStepExecution("step1")).getStepExecution()));

        // when & then
        assertThatThrownBy(() -> tasklet.execute(contribution, chunkContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Region not found");

        verifyNoInteractions(mockUseCase);
    }
}
