package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemClass {

    @Id
    private Long id;
    @Embedded
    private Language names;

    public ItemClass(Long id, Language names) {
        this.id = id;
        this.names = names;
    }

    public ItemClass(ItemClassesIndexResponse.ItemClass response) {
        this.id = response.getId();
        this.names = response.getName();
    }
}
