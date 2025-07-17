package com.worbes.adapter.jpa.auction;

import com.worbes.application.realm.model.RegionType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Primary
public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long>, AuctionRepository {
    @Query("""
                SELECT a.id FROM AuctionEntity a
                WHERE a.region = :region
                  AND ((:realmId IS NULL AND a.realmId IS NULL) OR a.realmId = :realmId)
                  AND a.endedAt IS NULL
                ORDER BY a.id ASC
            """)
    Page<Long> findActiveAuctionsByRegionAndRealmId(
            @Param("region") RegionType region,
            @Param("realmId") Long realmId,
            Pageable pageable
    );

    @Query("""
                SELECT DISTINCT a.itemId FROM AuctionEntity a
                WHERE a.endedAt IS NULL
            """)
    Page<Long> findDistinctActiveItemIds(Pageable pageable);
}
