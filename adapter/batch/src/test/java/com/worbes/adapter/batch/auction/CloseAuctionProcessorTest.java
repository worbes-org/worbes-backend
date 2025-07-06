package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Map;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_SNAPSHOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringJUnitConfig(CloseAuctionProcessor.class)
@SpringBatchTest
class CloseAuctionProcessorTest {

    @Autowired
    private CloseAuctionProcessor closeAuctionProcessor;

    private StepExecution getStepExecutionWithSnapshot(Object snapshot) {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution()
                .getExecutionContext()
                .put(AUCTION_SNAPSHOT.getKey(), snapshot);
        return stepExecution;
    }

    public StepExecution getStepExecution() {
        List<Auction> auctions = List.of(
                Auction.builder().id(1L).build(),
                Auction.builder().id(2L).build()
        );

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution()
                .getExecutionContext()
                .put(AUCTION_SNAPSHOT.getKey(), auctions);

        return stepExecution;
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("활성화된 경매 ID를 필터링 한다.")
        void shouldFilterOutActiveAuctionIds() {
            // given
            List<Auction> auctions = List.of(
                    Auction.builder().id(1L).build(),
                    Auction.builder().id(2L).build()
            );
            closeAuctionProcessor.beforeStep(getStepExecutionWithSnapshot(auctions));

            // when
            Long result1 = closeAuctionProcessor.process(1L); // exists in snapshot
            Long result2 = closeAuctionProcessor.process(3L); // not in snapshot

            // then
            assertThat(result1).isNull();             // 필터됨
            assertThat(result2).isEqualTo(3L);        // 유지됨
        }
    }

    @Nested
    @DisplayName("경계 케이스(edge)")
    class EdgeCases {
        @Test
        @DisplayName("auctionId가 null이면 null 반환")
        void shouldReturnNullWhenAuctionIdIsNull() {
            List<Auction> auctions = List.of(Auction.builder().id(1L).build());
            closeAuctionProcessor.beforeStep(getStepExecutionWithSnapshot(auctions));
            Long result = closeAuctionProcessor.process(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("snapshot에 Auction이 아닌 객체가 포함되어도 Auction만 필터링에 사용")
        void shouldIgnoreNonAuctionObjectsInSnapshot() {
            List<Object> snapshot = List.of(
                    Auction.builder().id(1L).build(),
                    Map.of("id", 2L), // Auction이 아님
                    "string"
            );
            closeAuctionProcessor.beforeStep(getStepExecutionWithSnapshot(snapshot));
            Long result1 = closeAuctionProcessor.process(1L);
            Long result2 = closeAuctionProcessor.process(2L);
            assertThat(result1).isNull(); // Auction만 필터됨
            assertThat(result2).isEqualTo(2L); // Auction이 아니면 무시
        }
    }

    @Nested
    @DisplayName("실패 케이스(fail)")
    class FailCases {
        @Test
        @DisplayName("ExecutionContext에 snapshot이 null이면 예외 발생")
        void shouldThrowWhenSnapshotIsNull() {
            StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
            assertThatThrownBy(() -> closeAuctionProcessor.beforeStep(stepExecution))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid snapshot data in ExecutionContext");
        }

        @Test
        @DisplayName("ExecutionContext에 snapshot이 List가 아니면 예외 발생")
        void shouldThrowWhenSnapshotIsNotList() {
            closeAuctionProcessor = new CloseAuctionProcessor();
            StepExecution stepExecution = getStepExecutionWithSnapshot("not a list");
            assertThatThrownBy(() -> closeAuctionProcessor.beforeStep(stepExecution))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid snapshot data in ExecutionContext");
        }
    }
}
