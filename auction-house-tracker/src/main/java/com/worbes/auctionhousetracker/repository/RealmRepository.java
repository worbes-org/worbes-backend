package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RealmRepository extends JpaRepository<Realm, Long> {

    @Query("SELECT DISTINCT r.connectedRealmId FROM Realm r WHERE r.region = :region")
    List<Long> findDistinctConnectedRealmIdsByRegion(@Param("region") Region region);
}
