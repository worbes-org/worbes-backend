package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.DeleteAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;

import static com.worbes.adapter.batch.auction.SyncAuctionParameter.REALM_ID;
import static com.worbes.adapter.batch.auction.SyncAuctionParameter.REGION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAuctionTaskletTest {
    @Test
    @DisplayName("정상 실행 시 DeleteAuctionUseCase가 호출되고 writeCount가 증가한다")
    void execute_validParameters_callsUseCaseAndUpdatesWriteCount() throws Exception {
        // given
        DeleteAuctionUseCase mockUseCase = mock(DeleteAuctionUseCase.class);
        DeleteAuctionTasklet tasklet = new DeleteAuctionTasklet(mockUseCase);

        long expectedDeleted = 5L;
        given(mockUseCase.execute(RegionType.KR, 123L)).willReturn(expectedDeleted);

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
        verify(mockUseCase).execute(RegionType.KR, 123L);
        verify(contribution).incrementWriteCount(expectedDeleted);
    }

    @Test
    @DisplayName("Region 파라미터가 없으면 IllegalArgumentException 발생")
    void execute_missingRegion_throwsException() {
        // given
        DeleteAuctionUseCase mockUseCase = mock(DeleteAuctionUseCase.class);
        DeleteAuctionTasklet tasklet = new DeleteAuctionTasklet(mockUseCase);

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
