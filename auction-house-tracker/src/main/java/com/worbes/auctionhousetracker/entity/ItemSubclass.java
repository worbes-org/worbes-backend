package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.ItemSubclassResponse;
import com.worbes.auctionhousetracker.entity.embeded.ItemSubclassId;
import com.worbes.auctionhousetracker.entity.embeded.Translation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSubclass {

    @EmbeddedId
    private ItemSubclassId id;

    @ManyToOne
    @MapsId("itemClassId")
    private ItemClass itemClass;

    @Embedded
    private Translation displayName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "en_US", column = @Column(name = "verbose_en_US")),
            @AttributeOverride(name = "es_MX", column = @Column(name = "verbose_es_MX")),
            @AttributeOverride(name = "pt_BR", column = @Column(name = "verbose_pt_BR")),
            @AttributeOverride(name = "de_DE", column = @Column(name = "verbose_de_DE")),
            @AttributeOverride(name = "en_GB", column = @Column(name = "verbose_en_GB")),
            @AttributeOverride(name = "es_ES", column = @Column(name = "verbose_es_ES")),
            @AttributeOverride(name = "fr_FR", column = @Column(name = "verbose_fr_FR")),
            @AttributeOverride(name = "it_IT", column = @Column(name = "verbose_it_IT")),
            @AttributeOverride(name = "ru_RU", column = @Column(name = "verbose_ru_RU")),
            @AttributeOverride(name = "ko_KR", column = @Column(name = "verbose_ko_KR")),
            @AttributeOverride(name = "zh_TW", column = @Column(name = "verbose_zh_TW")),
            @AttributeOverride(name = "zh_CN", column = @Column(name = "verbose_zh_CN"))
    })
    private Translation verboseName;

    public ItemSubclass(ItemSubclassId id, ItemClass itemClass, Translation displayName, Translation verboseName) {
        this.id = id;
        this.itemClass = itemClass;
        this.displayName = displayName;
        this.verboseName = verboseName;
    }

    public ItemSubclass(ItemClass itemClass, ItemSubclassResponse response) {
        this.id = new ItemSubclassId(itemClass.getId(), response.getId());
        this.itemClass = itemClass;
        this.displayName = response.getDisplayName();
        this.verboseName = response.getVerboseName();
    }
}

