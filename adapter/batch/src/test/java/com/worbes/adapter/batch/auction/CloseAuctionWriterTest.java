package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.CloseAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.*;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Slf4j
@SpringJUnitConfig(CloseAuctionWriter.class)
@SpringBatchTest
class CloseAuctionWriterTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 1L;

    @MockBean
    private CloseAuctionUseCase closeAuctionUseCase;

    @Autowired
    private CloseAuctionWriter closeAuctionWriter;

    private StepExecution getStepExecutionWithParams(JobParameters jobParameters) {
        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    private StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now())
                .addLong(REALM_ID.getKey(), realmId)
                .toJobParameters();
        return getStepExecutionWithParams(jobParameters);
    }

    @AfterEach
    void clearMock() {
        clearInvocations(closeAuctionUseCase);
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("경매 ID 목록이 존재하면 해당 경매들을 종료 처리한다")
        void shouldCallCloseAuctionUseCaseWithCorrectCommand_whenAuctionIdsExist() {
            // given
            Set<Long> auctionIds = Set.of(101L, 102L, 103L);
            List<Long> chunk = new ArrayList<>(auctionIds);
            closeAuctionWriter.beforeStep(getStepExecution());

            // when
            closeAuctionWriter.write(new Chunk<>(chunk));

            // then
            then(closeAuctionUseCase).should()
                    .closeAll(region, realmId, auctionIds);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("chunk가 비어있으면 closeAll이 호출되지 않는다")
        void shouldNotCallCloseAllWhenChunkIsEmpty() {
            closeAuctionWriter.beforeStep(getStepExecution());
            closeAuctionWriter.write(new Chunk<>(Collections.emptyList()));
            then(closeAuctionUseCase).should(never()).closeAll(any(), any(), any());
        }

        @Test
        @DisplayName("chunk에 중복된 ID가 있으면 Set으로 중복 제거되어 전달된다")
        void shouldRemoveDuplicatesInChunk() {
            List<Long> chunk = Arrays.asList(1L, 2L, 2L, 3L, 1L);
            closeAuctionWriter.beforeStep(getStepExecution());
            closeAuctionWriter.write(new Chunk<>(chunk));
            then(closeAuctionUseCase).should().closeAll(region, realmId, Set.of(1L, 2L, 3L));
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
            StepExecution stepExecution = getStepExecutionWithParams(jobParameters);
            assertThatThrownBy(() -> closeAuctionWriter.beforeStep(stepExecution))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Region");
        }

        @Test
        @DisplayName("closeAll에서 예외 발생 시 예외가 전파된다")
        void shouldPropagateExceptionWhenCloseAllFails() {
            Set<Long> auctionIds = Set.of(1L, 2L);
            List<Long> chunk = new ArrayList<>(auctionIds);
            closeAuctionWriter.beforeStep(getStepExecution());
            doThrow(new RuntimeException("fail!"))
                    .when(closeAuctionUseCase).closeAll(region, realmId, auctionIds);
            assertThatThrownBy(() -> closeAuctionWriter.write(new Chunk<>(chunk)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("fail!");
        }
    }
}
