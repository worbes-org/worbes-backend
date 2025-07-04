package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.FetchAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("Integration::FetchAuctionTasklet")
@SpringJUnitConfig(FetchAuctionTasklet.class)
@SpringBatchTest
class FetchAuctionTaskletTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 1L;

    @MockBean
    FetchAuctionUseCase fetchAuctionUseCase;

    @Autowired
    Tasklet fetchAuctionTasklet;

    public StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now())
                .addLong(REALM_ID.getKey(), realmId)
                .toJobParameters();
        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

    @Test
    public void shouldFetchAuctionsAndStoreInExecutionContext() throws Exception {
        List<Auction> expected = createAuctions(1000);
        given(fetchAuctionUseCase.fetchAuctions(region, realmId)).willReturn(expected);

        StepExecution stepExecution = getStepExecution();
        StepContribution stepContribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        //when
        RepeatStatus repeatStatus = fetchAuctionTasklet.execute(stepContribution, chunkContext);

        //then
        assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
        assertThat(stepExecution.getJobExecution().getExecutionContext().get(AUCTION_SNAPSHOT.getKey())).isEqualTo(expected);
        assertThat(stepExecution.getJobExecution().getExecutionContext().get(AUCTION_COUNT.getKey())).isEqualTo(expected.size());

        then(fetchAuctionUseCase).should(times(1)).fetchAuctions(region, realmId);
    }

    private List<Auction> createAuctions(int count) {
        List<Auction> auctions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Auction auction = Auction.builder()
                    .id((long) count)
                    .itemId(1002L)
                    .realmId(1234L)
                    .quantity(5L)
                    .price(8000L)
                    .region(RegionType.KR)
                    .build();
            auctions.add(auction);
        }
        return auctions;
    }
}
