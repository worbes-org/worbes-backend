package com.worbes.adapter.mybatis.auction;

import com.worbes.application.auction.port.out.SaveAuctionSnapshotPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class AuctionSnapshotMybatisRepository implements SaveAuctionSnapshotPort {

    private final AuctionSnapshotMapper mapper;

    @Override
    public int save(RegionType region, Long realmId, Instant time) {
        Timestamp timestamp = Timestamp.from(time);
        if (realmId == null) {
            return mapper.insertRegionWide(region.name(), timestamp);
        }
        return mapper.insertRealmSpecific(region.name(), realmId, timestamp);
    }
}
