package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.AUCTION_SNAPSHOT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Integration::CloseAuctionProcessor")
@SpringJUnitConfig(CloseAuctionProcessor.class)
@SpringBatchTest
class CloseAuctionProcessorTest {

    @Autowired
    private CloseAuctionProcessor closeAuctionProcessor;

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

    @Test
    @DisplayName("활성화된 경매 ID를 필터링 한다.")
    void shouldFilterOutActiveAuctionIds() {
        // given
        closeAuctionProcessor.beforeStep(getStepExecution());

        // when
        Long result1 = closeAuctionProcessor.process(1L); // exists in snapshot
        Long result2 = closeAuctionProcessor.process(3L); // not in snapshot

        // then
        assertThat(result1).isNull();             // 필터됨
        assertThat(result2).isEqualTo(3L);        // 유지됨
    }
}
