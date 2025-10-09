package com.worbes.adapter.batch.auction;

import com.worbes.application.auction.port.in.DeleteAuctionSnapshotUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class DeleteAuctionSnapshotTasklet implements Tasklet {

    private final DeleteAuctionSnapshotUseCase deleteAuctionSnapshotUseCase;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        long result = deleteAuctionSnapshotUseCase.execute();
        contribution.incrementWriteCount(result);

        return RepeatStatus.FINISHED;
    }
}
