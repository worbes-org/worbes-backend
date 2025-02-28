package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Realm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealmRepository extends JpaRepository<Realm, Long> {
}
