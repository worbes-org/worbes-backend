package com.worbes.adapter.mybatis.auction;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;

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
}
