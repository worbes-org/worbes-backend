package com.worbes.batch.scheduler;

import com.worbes.adapter.batch.auction.SyncAuctionJobRunner;
import com.worbes.adapter.batch.item.CreateItemJobRunner;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorbesBatchScheduler {

    private final SyncAuctionJobRunner syncAuctionJobRunner;
    private final CreateItemJobRunner createItemJobRunner;

    @Scheduled(cron = "0 0 * * * *")
    public void runSyncAuctionJob() {
        syncAuctionJobRunner.run(RegionType.KR);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void runCreateItemJob() {
        createItemJobRunner.run();
    }
}
