package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.embeded.Translation;
import com.worbes.auctionhousetracker.entity.enums.Region;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Realm {

    @Id
    private Long id;

    private Translation name;

    private Long connectedRealmId;

    @Enumerated(EnumType.STRING)
    private Region region;

    @Builder
    private Realm(Long id, Translation name, Long connectedRealmId, Region region) {
        this.id = id;
        this.name = name;
        this.connectedRealmId = connectedRealmId;
        this.region = region;
    }
}
