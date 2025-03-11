package com.worbes.auctionhousetracker.entity;

import com.worbes.auctionhousetracker.dto.response.ItemResponse;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.Quality;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class Item {

    @Id
    private Long id;

    @Embedded
    private Language name;

    private Long itemClassId;

    private Long itemSubclassId;

    @Enumerated(EnumType.STRING)
    private Quality quality;

    private Integer itemLevel;

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String previewItem;

    private String iconUrl;

    // private 생성자 (빌더가 사용)
    @Builder(access = AccessLevel.PRIVATE)
    private Item(Long id, Language name, Long itemClassId, Long itemSubclassId,
                 Quality quality, Integer itemLevel, String previewItem, String iconUrl) {
        this.id = id;
        this.name = name;
        this.itemClassId = itemClassId;
        this.itemSubclassId = itemSubclassId;
        this.quality = quality;
        this.itemLevel = itemLevel;
        this.previewItem = previewItem;
        this.iconUrl = iconUrl;
    }

    // 정적 팩토리 메서드
    public static Item from(ItemResponse response, String iconUrl) {
        return Item.builder()
                .id(response.getId())
                .name(response.getName())
                .itemClassId(response.getItemClassId())
                .itemSubclassId(response.getItemSubclassId())
                .quality(response.getQuality())
                .itemLevel(response.getLevel())
                .previewItem(response.getPreviewItem())
                .iconUrl(iconUrl)
                .build();
    }
}
