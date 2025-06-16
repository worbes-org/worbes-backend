package com.worbes.adapter.jpa.repository.realm;

import com.worbes.adapter.jpa.entity.RealmEntity;
import com.worbes.application.realm.model.RegionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RealmJpaRepository extends JpaRepository<RealmEntity, Long> {
    @Query("SELECT r.slug FROM RealmEntity r WHERE r.region = :region")
    Set<String> findSlugByRegion(@Param("region") RegionType region);

    @Query("SELECT DISTINCT r.connectedRealmId FROM RealmEntity r WHERE r.region = :region")
    List<Long> findDistinctConnectedRealmIdsByRegion(@Param("region") RegionType region);
}
