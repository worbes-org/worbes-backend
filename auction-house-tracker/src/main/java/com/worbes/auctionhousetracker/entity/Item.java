package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.Quality;
import jakarta.persistence.*;
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

    @Embedded
    private Language name;

    private Long itemClassId;

    private Long itemSubclassId;

    @Enumerated(EnumType.STRING)
    private Quality quality;

    private Long itemLevel;

    @Column(columnDefinition = "jsonb")
    private String preview;
}
