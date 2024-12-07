package com.worbes.auctionhousetracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Item {

    @Id
    private Long id;

    private String name;

    public Item(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
