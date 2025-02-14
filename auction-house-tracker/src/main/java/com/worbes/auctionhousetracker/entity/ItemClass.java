package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ItemClass itemClass = (ItemClass) object;
        return Objects.equals(id, itemClass.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
