package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.embeded.Language;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemClass {

    @Id
    private Long id;
    @Embedded
    private Language names;
    @OneToMany
    private List<ItemSubclass> itemSubclasses;

    public ItemClass(Long id, Language names) {
        this.id = id;
        this.names = names;
    }
}
