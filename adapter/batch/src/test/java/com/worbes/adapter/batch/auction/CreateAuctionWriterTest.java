package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SaveAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameter.AUCTION_COUNT;
import static com.worbes.adapter.batch.auction.SyncAuctionParameter.AUCTION_SNAPSHOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CreateAuctionWriterTest {

    @Mock
    private SaveAuctionUseCase saveAuctionUseCase;

    private CreateAuctionWriter createAuctionWriter;

    @BeforeEach
    void setUp() {
        createAuctionWriter = new CreateAuctionWriter(saveAuctionUseCase);
    }

    @Test
    @DisplayName("빈 청크가 전달되면 아무 처리하지 않는다")
    void write_emptyChunk_doesNothing() {
        // given
        Chunk<Auction> emptyChunk = new Chunk<>();

        // when
        createAuctionWriter.write(emptyChunk);

        // then
        verifyNoInteractions(saveAuctionUseCase);
    }

    @Test
    @DisplayName("유효한 경매 데이터 청크를 저장한다")
    void write_validChunk_savesAuctions() {
        // given
        List<Auction> auctions = List.of(
                Auction.builder()
                        .id(1L)
                        .itemId(210930L)
                        .quantity(200)
                        .unitPrice(1000L)
                        .region(RegionType.KR)
                        .build(),
                Auction.builder()
                        .id(2L)
                        .itemId(212444L)
                        .quantity(1)
                        .buyout(1000L)
                        .realmId(205L)
                        .region(RegionType.KR)
                        .build()
        );

        Chunk<Auction> chunk = new Chunk<>(auctions);
        when(saveAuctionUseCase.execute(any(List.class))).thenReturn(2L);

        // when
        createAuctionWriter.write(chunk);

        // then
        verify(saveAuctionUseCase).execute(auctions);
    }

    @Test
    @DisplayName("여러 청크를 처리하며 총 처리된 경매 수를 누적한다")
    void write_multipleChunks_accumulatesTotal() {
        // given
        List<Auction> firstChunk = List.of(
                Auction.builder()
                        .id(1L)
                        .itemId(210930L)
                        .quantity(200)
                        .unitPrice(1000L)
                        .region(RegionType.KR)
                        .build()
        );

        List<Auction> secondChunk = List.of(
                Auction.builder()
                        .id(2L)
                        .itemId(212444L)
                        .quantity(1)
                        .buyout(1000L)
                        .realmId(205L)
                        .region(RegionType.KR)
                        .build(),
                Auction.builder()
                        .id(3L)
                        .itemId(212445L)
                        .quantity(2)
                        .buyout(2000L)
                        .realmId(206L)
                        .region(RegionType.US)
                        .build()
        );

        when(saveAuctionUseCase.execute(firstChunk)).thenReturn(1L);
        when(saveAuctionUseCase.execute(secondChunk)).thenReturn(2L);

        // when
        createAuctionWriter.write(new Chunk<>(firstChunk));
        createAuctionWriter.write(new Chunk<>(secondChunk));

        // then
        verify(saveAuctionUseCase).execute(firstChunk);
        verify(saveAuctionUseCase).execute(secondChunk);

        // afterStep에서 총합을 확인하기 위해 실행
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        createAuctionWriter.afterStep(stepExecution);

        assertThat(stepExecution.getWriteCount()).isEqualTo(3L); // 1 + 2
    }

    @Test
    @DisplayName("afterStep에서 StepExecution의 writeCount를 설정한다")
    void afterStep_setsWriteCount() {
        // given
        List<Auction> auctions = List.of(
                Auction.builder()
                        .id(1L)
                        .itemId(210930L)
                        .quantity(200)
                        .unitPrice(1000L)
                        .region(RegionType.KR)
                        .build()
        );

        when(saveAuctionUseCase.execute(any(List.class))).thenReturn(5L);
        createAuctionWriter.write(new Chunk<>(auctions));

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        // when
        ExitStatus exitStatus = createAuctionWriter.afterStep(stepExecution);

        // then
        assertThat(stepExecution.getWriteCount()).isEqualTo(5L);
        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
    }

    @Test
    @DisplayName("afterStep에서 JobExecution의 ExecutionContext에서 AUCTION_SNAPSHOT과 AUCTION_COUNT를 제거한다")
    void afterStep_removesSnapshotAndCountFromJobContext() {
        // given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();

        // ExecutionContext에 데이터 추가
        jobContext.put(AUCTION_SNAPSHOT.getKey(), List.of("test", "data"));
        jobContext.put(AUCTION_COUNT.getKey(), 100L);
        jobContext.put("other_key", "should_remain");

        // when
        ExitStatus exitStatus = createAuctionWriter.afterStep(stepExecution);

        // then
        assertThat(jobContext.containsKey(AUCTION_SNAPSHOT.getKey())).isFalse();
        assertThat(jobContext.containsKey(AUCTION_COUNT.getKey())).isFalse();
        assertThat(jobContext.containsKey("other_key")).isTrue(); // 다른 키는 유지
        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
    }

    @Test
    @DisplayName("write와 afterStep이 연동되어 정상 동작한다")
    void integration_writeAndAfterStep_worksCorrectly() {
        // given
        List<Auction> auctions = List.of(
                Auction.builder()
                        .id(1L)
                        .itemId(210930L)
                        .quantity(200)
                        .unitPrice(1000L)
                        .region(RegionType.KR)
                        .build(),
                Auction.builder()
                        .id(2L)
                        .itemId(212444L)
                        .quantity(1)
                        .buyout(1000L)
                        .realmId(205L)
                        .region(RegionType.KR)
                        .build()
        );

        when(saveAuctionUseCase.execute(any(List.class))).thenReturn(2L);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();

        jobContext.put(AUCTION_SNAPSHOT.getKey(), auctions);
        jobContext.put(AUCTION_COUNT.getKey(), 2L);

        // when
        createAuctionWriter.write(new Chunk<>(auctions));
        ExitStatus exitStatus = createAuctionWriter.afterStep(stepExecution);

        // then
        verify(saveAuctionUseCase).execute(auctions);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2L);
        assertThat(jobContext.containsKey(AUCTION_SNAPSHOT.getKey())).isFalse();
        assertThat(jobContext.containsKey(AUCTION_COUNT.getKey())).isFalse();
        assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
    }

    @Test
    @DisplayName("UseCase에서 예외가 발생하면 전파한다")
    void write_useCaseThrowsException_propagatesException() {
        // given
        List<Auction> auctions = List.of(
                Auction.builder()
                        .id(1L)
                        .itemId(210930L)
                        .quantity(200)
                        .unitPrice(1000L)
                        .region(RegionType.KR)
                        .build()
        );

        RuntimeException expectedException = new RuntimeException("Database error");
        when(saveAuctionUseCase.execute(any(List.class))).thenThrow(expectedException);

        // when & then
        assertThrows(RuntimeException.class, () -> createAuctionWriter.write(new Chunk<>(auctions)));
    }

    @Test
    @DisplayName("totalUpdatedOrInsertedAuction은 0부터 시작한다")
    void initialState_totalCountIsZero() {
        // given & when
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        createAuctionWriter.afterStep(stepExecution);

        // then
        assertThat(stepExecution.getWriteCount()).isEqualTo(0L);
    }
}
