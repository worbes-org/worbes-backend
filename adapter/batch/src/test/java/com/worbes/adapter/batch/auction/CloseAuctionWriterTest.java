package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.EndAuctionUseCase;
import com.worbes.application.realm.model.RegionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.worbes.adapter.batch.auction.SyncAuctionParameters.*;
import static org.mockito.BDDMockito.then;


@DisplayName("Integration::CloseAuctionWriter")
@SpringJUnitConfig(CloseAuctionWriter.class)
@SpringBatchTest
@Slf4j
class CloseAuctionWriterTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 1L;

    @MockBean
    private EndAuctionUseCase endAuctionUseCase;

    @Autowired
    private CloseAuctionWriter closeAuctionWriter;

    public StepExecution getStepExecution() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString(REGION.getKey(), region.name())
                .addLocalDateTime(AUCTION_DATE.getKey(), LocalDateTime.now())
                .addLong(REALM_ID.getKey(), realmId)
                .toJobParameters();
        return MetaDataInstanceFactory.createStepExecution(jobParameters);
    }

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
        then(endAuctionUseCase).should()
                .end(region, realmId, auctionIds);
    }
}
