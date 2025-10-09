package com.worbes.adapter.batch;

import com.worbes.adapter.batch.auction.DeleteAuctionSnapshotJobRunner;
import com.worbes.adapter.batch.auction.SyncAuctionJobRunner;
import com.worbes.adapter.batch.item.CreateItemJobRunner;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final SyncAuctionJobRunner syncAuctionJobRunner;
    private final CreateItemJobRunner createItemJobRunner;
    private final DeleteAuctionSnapshotJobRunner deleteAuctionSnapshotJobRunner;

    @Scheduled(cron = "0 0 * * * *")
    public void runSyncAuctionJob() {
        syncAuctionJobRunner.run(RegionType.KR);
    }

    @Scheduled(cron = "0 30 4 * * *")
    public void runCreateItemJob() {
        createItemJobRunner.run();
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void runDeleteAuctionSnapshotJob() {
        deleteAuctionSnapshotJobRunner.run();
    }
}
