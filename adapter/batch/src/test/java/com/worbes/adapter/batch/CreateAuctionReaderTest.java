package com.worbes.adapter.batch;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.worbes.adapter.batch.SyncAuctionParameters.AUCTION_SNAPSHOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@DisplayName("Integration::CreateAuctionReader")
@SpringJUnitConfig(CreateAuctionReader.class)
@SpringBatchTest
class CreateAuctionReaderTest {

    @Autowired
    private CreateAuctionReader createAuctionReader;

    @Test
    @DisplayName("beforeStep에서 ExecutionContext에 유효한 Auction 리스트가 있어야 한다")
    void beforeStep_validSnapshot_setsIterator() {
        // given
        List<Auction> auctions = List.of(
                Auction.builder().id(1L).region(RegionType.KR).build(),
                Auction.builder().id(2L).region(RegionType.KR).build()
        );

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().put(AUCTION_SNAPSHOT.getKey(), auctions);

        // when
        createAuctionReader.beforeStep(stepExecution);

        // then
        Auction firstRead = createAuctionReader.read();
        Auction secondRead = createAuctionReader.read();
        Auction thirdRead = createAuctionReader.read();

        assertThat(firstRead).isEqualTo(auctions.get(0));
        assertThat(secondRead).isEqualTo(auctions.get(1));
        assertThat(thirdRead).isNull();
    }

    @Test
    @DisplayName("beforeStep에서 ExecutionContext에 유효하지 않은 데이터가 있으면 예외 발생")
    void beforeStep_invalidSnapshot_throwsException() {
        // given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().put(AUCTION_SNAPSHOT.getKey(), "invalid_data");

        // when & then
        assertThatThrownBy(() -> createAuctionReader.beforeStep(stepExecution))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid snapshot data");
    }
}
