package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SyncAuctionUseCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Slf4j
@SpringJUnitConfig(SyncAuctionWriter.class)
@SpringBatchTest
class SyncAuctionWriterTest {

    @MockBean
    private SyncAuctionUseCase syncAuctionUseCase;

    private SyncAuctionWriter syncAuctionWriter;

    private StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution();
    }

    @BeforeEach
    void setUp() {
        syncAuctionWriter = new SyncAuctionWriter(syncAuctionUseCase);
    }

    @AfterEach
    void clearMock() {
        clearInvocations(syncAuctionUseCase);
        reset(syncAuctionUseCase);
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("경매 목록이 존재하면 syncAll이 호출되고 writeCount가 누적된다")
        void shouldSyncAndAccumulateWriteCount() {
            // given
            List<Auction> auctions = List.of(
                    Auction.builder().id(1L).build(),
                    Auction.builder().id(2L).build()
            );
            given(syncAuctionUseCase.execute(anyList())).willReturn(2);
            StepExecution stepExecution = getStepExecution();
            syncAuctionWriter.write(new Chunk<>(auctions));
            // when
            syncAuctionWriter.afterStep(stepExecution);
            // then
            then(syncAuctionUseCase).should().execute(auctions);
            assertThat(stepExecution.getWriteCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("경계 케이스")
    class EdgeCases {
        @Test
        @DisplayName("chunk가 비어있으면 syncAll이 호출되지 않고 writeCount는 0이다")
        void shouldNotCallSyncAllWhenChunkIsEmpty() {
            StepExecution stepExecution = getStepExecution();
            syncAuctionWriter.write(new Chunk<>(Collections.emptyList()));
            syncAuctionWriter.afterStep(stepExecution);
            then(syncAuctionUseCase).shouldHaveNoInteractions();
            assertThat(stepExecution.getWriteCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("대량 데이터도 정상 처리된다")
        void shouldHandleLargeChunk() {
            List<Auction> auctions = new ArrayList<>();
            for (long i = 1; i <= 1000; i++) {
                auctions.add(Auction.builder().id(i).build());
            }
            given(syncAuctionUseCase.execute(anyList())).willReturn(1000);
            StepExecution stepExecution = getStepExecution();
            syncAuctionWriter.write(new Chunk<>(auctions));
            syncAuctionWriter.afterStep(stepExecution);
            then(syncAuctionUseCase).should().execute(auctions);
            assertThat(stepExecution.getWriteCount()).isEqualTo(1000);
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class FailCases {
        @Test
        @DisplayName("syncAll에서 예외 발생 시 예외가 전파된다")
        void shouldPropagateExceptionWhenSyncAllFails() {
            List<Auction> auctions = List.of(Auction.builder().id(1L).build());
            doThrow(new RuntimeException("fail!"))
                    .when(syncAuctionUseCase).execute(anyList());
            assertThatThrownBy(() -> syncAuctionWriter.write(new Chunk<>(auctions)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("fail!");
        }
    }
} 
