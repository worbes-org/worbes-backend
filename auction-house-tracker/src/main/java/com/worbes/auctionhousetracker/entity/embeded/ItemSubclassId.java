package com.worbes.auctionhousetracker.entity.embeded;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ItemSubclassId implements Serializable {
    private Long itemClassId;
    private Long itemSubclassId;
}
