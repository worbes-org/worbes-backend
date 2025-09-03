package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameter.AUCTION_SNAPSHOT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class CreateAuctionReaderTest {

    @Test
    @DisplayName("beforeStep에서 ExecutionContext에 유효한 Auction 리스트가 있어야 한다")
    void beforeStep_validSnapshot_setsIterator() {
        // given
        CreateAuctionReader createAuctionReader = new CreateAuctionReader();

        List<Auction> auctions = List.of(
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
        CreateAuctionReader createAuctionReader = new CreateAuctionReader();
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().put(AUCTION_SNAPSHOT.getKey(), "invalid_data");

        // when & then
        assertThatThrownBy(() -> createAuctionReader.beforeStep(stepExecution))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid snapshot data");
    }

    @Test
    @DisplayName("빈 리스트가 제공되면 read()는 null을 반환한다")
    void beforeStep_emptyList_readReturnsNull() {
        // given
        CreateAuctionReader createAuctionReader = new CreateAuctionReader();
        List<Auction> emptyAuctions = List.of();

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().put(AUCTION_SNAPSHOT.getKey(), emptyAuctions);

        // when
        createAuctionReader.beforeStep(stepExecution);

        // then
        assertThat(createAuctionReader.read()).isNull();
    }

    @Test
    @DisplayName("리스트에 Auction이 아닌 객체가 있으면 필터링된다")
    void beforeStep_mixedList_filtersNonAuctionObjects() {
        // given
        CreateAuctionReader createAuctionReader = new CreateAuctionReader();
        Auction validAuction = Auction.builder()
                .id(1L)
                .itemId(210930L)
                .quantity(200)
                .unitPrice(1000L)
                .region(RegionType.KR)
                .build();

        List<Object> mixedList = List.of(validAuction, "invalid_string", 123);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        stepExecution.getJobExecution().getExecutionContext().put(AUCTION_SNAPSHOT.getKey(), mixedList);

        // when
        createAuctionReader.beforeStep(stepExecution);

        // then
        Auction firstRead = createAuctionReader.read();
        Auction secondRead = createAuctionReader.read();

        assertThat(firstRead).isEqualTo(validAuction);
        assertThat(secondRead).isNull(); // Only one valid Auction object
    }
}
