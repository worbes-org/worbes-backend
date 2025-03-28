package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.entity.enums.InventoryType;
import com.worbes.auctionhousetracker.entity.enums.QualityType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Item {

    @Id
    private Long id;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, String> name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_class_id", nullable = false)
    private ItemClass itemClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_subclass_id", nullable = false)
    private ItemSubclass itemSubclass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QualityType quality;

    @Column(nullable = false)
    private Integer level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryType inventoryType;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> previewItem;

    @Column(nullable = false)
    private String iconUrl;
}
