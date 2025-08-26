package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetAuctionTrendQuery;
import com.worbes.application.auction.port.out.FindAuctionTrendPort;
import com.worbes.application.auction.port.out.SaveAuctionSnapshotPort;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuctionSnapshotMybatisAdapter implements SaveAuctionSnapshotPort, FindAuctionTrendPort {

    private final AuctionSnapshotMapper mapper;

    @Override
    public int saveAll(RegionType region, Long realmId, Instant time) {
        Timestamp timestamp = Timestamp.from(time);
        if (realmId == null) {
            return mapper.insertRegionWide(region.name(), timestamp);
        }
        return mapper.insertRealmSpecific(region.name(), realmId, timestamp);
    }

    @Override
    public List<AuctionTrendPoint> findTrendsBy(GetAuctionTrendQuery query) {
        return mapper.findTrendsBy(query);
    }
}
