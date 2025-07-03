package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionJpaRepository extends JpaRepository<AuctionEntity, Long> {
}
