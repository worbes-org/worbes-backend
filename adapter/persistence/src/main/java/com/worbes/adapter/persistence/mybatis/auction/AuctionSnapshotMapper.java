package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetAuctionTrendQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface AuctionSnapshotMapper {

    int insertRealmSpecific(
            @Param("region") String region,
            @Param("realmId") Long realmId,
            @Param("time") Timestamp time
    );

    int insertRegionWide(
            @Param("region") String region,
            @Param("time") Timestamp time
    );

    List<AuctionTrendPoint> findTrendsBy(@Param("query") GetAuctionTrendQuery query);
}
