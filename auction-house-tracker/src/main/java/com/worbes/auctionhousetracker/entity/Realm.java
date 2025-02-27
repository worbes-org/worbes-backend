package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.embeded.Language;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Realm {

    @Id
    private Long id;

    private Language name;

    private Long connectedRealmId;
}
