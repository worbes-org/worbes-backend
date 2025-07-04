package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.CreateAuctionUseCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_COUNT;
import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_SNAPSHOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@Slf4j
@DisplayName("Integration::CreateAuctionWriter")
@SpringJUnitConfig(CreateAuctionWriter.class)
@SpringBatchTest
class CreateAuctionWriterTest {

    @MockBean
    private CreateAuctionUseCase createAuctionUseCase;

    private CreateAuctionWriter createAuctionWriter;

    @BeforeEach
    void setUp() {
        createAuctionWriter = new CreateAuctionWriter(createAuctionUseCase);
    }

    @Test
    @DisplayName("open() 메서드가 저장된 totalSavedCount를 복원한다")
    void open_restoresTotalSavedCount() {
        ExecutionContext context = new ExecutionContext();
        context.putInt("createAuctionWriter.totalSavedCount", 42);

        createAuctionWriter.open(context);

        // 내부 상태 totalSavedCount가 42로 설정되었는지 직접 검증할 방법이 없으므로 write() 호출하여 간접 검증
        given(createAuctionUseCase.createAuctions(anyList())).willReturn(10);

        List<Auction> auctions = List.of(mock(Auction.class));
        createAuctionWriter.write(new Chunk<>(auctions));

        // totalSavedCount = 42 + 10 = 52가 되어야 하므로 다음 update() 시 context에 52가 저장될 것
        ExecutionContext updatedContext = new ExecutionContext();
        createAuctionWriter.update(updatedContext);
        assertThat(updatedContext.getInt("createAuctionWriter.totalSavedCount")).isEqualTo(52);
    }

    @Test
    @DisplayName("write() 호출 시 CreateAuctionUseCase가 호출되고 totalSavedCount가 증가한다")
    void write_callsUseCaseAndIncrementsCount() {
        List<Auction> auctions = List.of(mock(Auction.class), mock(Auction.class));
        given(createAuctionUseCase.createAuctions(anyList())).willReturn(2);

        createAuctionWriter.write(new Chunk<>(auctions));

        then(createAuctionUseCase).should().createAuctions(auctions);
    }

    @Test
    @DisplayName("afterStep()는 총 저장 수와 auctionCount에 따라 성공 또는 실패 상태를 반환한다")
    void afterStep_returnsCorrectExitStatus() {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();

        // 총 경매 개수 세팅
        jobContext.putInt(AUCTION_COUNT.getKey(), 5);
        jobContext.put(AUCTION_SNAPSHOT.getKey(), List.of());

        StepExecution stepExecution = new StepExecution("step", jobExecution);
        createAuctionWriter.open(new ExecutionContext()); // 초기화
        // 총 저장 수 3으로 임의 설정 (내부 totalSavedCount 필드를 직접 설정할 방법 없으므로 write() 호출로 간접 설정)
        given(createAuctionUseCase.createAuctions(anyList())).willReturn(3);
        createAuctionWriter.write(new Chunk<>(List.of(mock(Auction.class), mock(Auction.class), mock(Auction.class))));

        ExitStatus exitStatus = createAuctionWriter.afterStep(stepExecution);

        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
        assertThat(stepExecution.getWriteCount()).isEqualTo(3);
        assertThat(stepExecution.getFilterCount()).isEqualTo(2);

        // totalSavedCount가 auctionCount보다 클 경우 실패 반환
        jobContext.putInt(AUCTION_COUNT.getKey(), 2);
        ExitStatus failStatus = createAuctionWriter.afterStep(stepExecution);
        assertThat(failStatus).isEqualTo(ExitStatus.FAILED);
    }

    @Test
    @DisplayName("afterStep() 호출 시 ExecutionContext의 임시 데이터가 삭제된다")
    void afterStep_cleansExecutionContext() {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();

        jobContext.putInt(AUCTION_COUNT.getKey(), 1);
        jobContext.put(AUCTION_SNAPSHOT.getKey(), List.of(mock(Auction.class)));
        jobContext.put("createAuctionWriter.totalSavedCount", 1);

        StepExecution stepExecution = new StepExecution("step", jobExecution);
        given(createAuctionUseCase.createAuctions(anyList())).willReturn(1);
        createAuctionWriter.write(new Chunk<>(List.of(mock(Auction.class))));

        createAuctionWriter.afterStep(stepExecution);

        assertThat(jobContext.containsKey(AUCTION_SNAPSHOT.getKey())).isFalse();
        assertThat(jobContext.containsKey(AUCTION_COUNT.getKey())).isFalse();
        assertThat(jobContext.containsKey("createAuctionWriter.totalSavedCount")).isFalse();
    }
}
