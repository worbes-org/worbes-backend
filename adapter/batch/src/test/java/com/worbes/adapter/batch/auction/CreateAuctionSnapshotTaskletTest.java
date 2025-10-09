package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.CreateAuctionSnapshotUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.worbes.adapter.batch.auction.SyncAuctionParameter.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CreateAuctionSnapshotTaskletTest {

    @Mock
    private CreateAuctionSnapshotUseCase createAuctionSnapshotUseCase;

    @Mock
    private StepContribution stepContribution;

    @Mock
    private ChunkContext chunkContext;

    @Mock
    private StepContext stepContext;

    private CreateAuctionSnapshotTasklet tasklet;

    @BeforeEach
    void setUp() {
        tasklet = new CreateAuctionSnapshotTasklet(createAuctionSnapshotUseCase);
    }

    @Test
    @DisplayName("모든 필수 파라미터가 있으면 정상적으로 실행된다")
    void execute_withAllRequiredParameters_executesSuccessfully() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        Long realmId = 205L;
        RegionType region = RegionType.KR;

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .addLong(REALM_ID.getKey(), realmId)
                .toJobParameters();

        setupChunkContext(jobParameters);

        Instant expectedInstant = auctionDate.atZone(ZoneId.systemDefault()).toInstant();
        when(createAuctionSnapshotUseCase.execute(region, realmId, expectedInstant)).thenReturn(100);

        // when
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);

        // then
        assertThat(result).isEqualTo(RepeatStatus.FINISHED);
        verify(createAuctionSnapshotUseCase).execute(region, realmId, expectedInstant);
        verify(stepContribution).incrementWriteCount(100);
    }

    @Test
    @DisplayName("realmId가 null이어도 정상적으로 실행된다")
    void execute_withNullRealmId_executesSuccessfully() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        RegionType region = RegionType.US;

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .toJobParameters();

        setupChunkContext(jobParameters);

        Instant expectedInstant = auctionDate.atZone(ZoneId.systemDefault()).toInstant();
        when(createAuctionSnapshotUseCase.execute(region, null, expectedInstant)).thenReturn(50);

        // when
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);

        // then
        assertThat(result).isEqualTo(RepeatStatus.FINISHED);
        verify(createAuctionSnapshotUseCase).execute(region, null, expectedInstant);
        verify(stepContribution).incrementWriteCount(50);
    }

    @Test
    @DisplayName("region 파라미터가 없으면 IllegalArgumentException이 발생한다")
    void execute_withoutRegion_throwsIllegalArgumentException() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .addLong(REALM_ID.getKey(), 205L)
                .toJobParameters();

        setupChunkContext(jobParameters);

        // when & then
        assertThatThrownBy(() -> tasklet.execute(stepContribution, chunkContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Region not found");
    }

    @Test
    @DisplayName("잘못된 region 값이면 IllegalArgumentException이 발생한다")
    void execute_withInvalidRegion_throwsIllegalArgumentException() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), "INVALID_REGION")
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .addLong(REALM_ID.getKey(), 205L)
                .toJobParameters();

        setupChunkContext(jobParameters);

        // when & then
        assertThatThrownBy(() -> tasklet.execute(stepContribution, chunkContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant");
    }

    @Test
    @DisplayName("auction date 파라미터가 없으면 IllegalArgumentException이 발생한다")
    void execute_withoutAuctionDate_throwsIllegalArgumentException() {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), RegionType.KR.name())
                .addLong(REALM_ID.getKey(), 205L)
                .toJobParameters();

        setupChunkContext(jobParameters);

        // when & then
        assertThatThrownBy(() -> tasklet.execute(stepContribution, chunkContext))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Time not found");
    }

    @Test
    @DisplayName("UseCase 실행 결과가 0이어도 정상적으로 처리된다")
    void execute_withZeroResult_executesSuccessfully() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        RegionType region = RegionType.KR;

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .toJobParameters();

        setupChunkContext(jobParameters);

        Instant expectedInstant = auctionDate.atZone(ZoneId.systemDefault()).toInstant();
        when(createAuctionSnapshotUseCase.execute(region, null, expectedInstant)).thenReturn(0);

        // when
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);

        // then
        assertThat(result).isEqualTo(RepeatStatus.FINISHED);
        verify(createAuctionSnapshotUseCase).execute(region, null, expectedInstant);
        verify(stepContribution).incrementWriteCount(0);
    }

    @Test
    @DisplayName("UseCase에서 예외가 발생하면 전파된다")
    void execute_useCaseThrowsException_propagatesException() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        RegionType region = RegionType.KR;

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .toJobParameters();

        setupChunkContext(jobParameters);

        Instant expectedInstant = auctionDate.atZone(ZoneId.systemDefault()).toInstant();
        RuntimeException expectedException = new RuntimeException("Database connection failed");
        when(createAuctionSnapshotUseCase.execute(region, null, expectedInstant))
                .thenThrow(expectedException);

        // when & then
        assertThatThrownBy(() -> tasklet.execute(stepContribution, chunkContext))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database connection failed");
    }

    @Test
    @DisplayName("Instant 변환이 올바르게 수행된다")
    void execute_instantConversion_isCorrect() {
        // given
        LocalDateTime auctionDate = LocalDateTime.of(2024, 8, 22, 15, 45, 30);
        RegionType region = RegionType.KR;

        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), auctionDate)
                .toJobParameters();

        setupChunkContext(jobParameters);

        // 예상되는 Instant 값
        Instant expectedInstant = auctionDate.atZone(ZoneId.systemDefault()).toInstant();
        when(createAuctionSnapshotUseCase.execute(region, null, expectedInstant)).thenReturn(75);

        // when
        tasklet.execute(stepContribution, chunkContext);

        // then
        verify(createAuctionSnapshotUseCase).execute(
                eq(region),
                isNull(),
                eq(expectedInstant)
        );
    }

    private void setupChunkContext(JobParameters jobParameters) {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution("testJob", 1L, 1L, jobParameters);
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobExecution, "testStep", 1L);

        when(chunkContext.getStepContext()).thenReturn(stepContext);
        when(stepContext.getStepExecution()).thenReturn(stepExecution);
    }
}
