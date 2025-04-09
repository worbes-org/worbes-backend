package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RealmRepository extends JpaRepository<Realm, Long> {

    @Query("SELECT DISTINCT r.connectedRealmId FROM Realm r WHERE r.region = :region")
    List<Long> findDistinctConnectedRealmIdsByRegion(@Param("region") RegionType region);

    List<Realm> findByRegion(RegionType region);

    Optional<Realm> findByIdAndRegion(Long id, RegionType region);
}
