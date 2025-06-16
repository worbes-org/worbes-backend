package com.worbes.adapter.jpa.repository.item;

import com.worbes.adapter.jpa.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemJpaRepository extends JpaRepository<ItemEntity, Long> {
}
