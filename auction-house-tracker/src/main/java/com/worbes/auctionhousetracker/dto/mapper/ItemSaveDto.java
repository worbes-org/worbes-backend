package com.worbes.auctionhousetracker.dto.mapper;

import com.worbes.auctionhousetracker.entity.enums.InventoryType;
import com.worbes.auctionhousetracker.entity.enums.QualityType;
import lombok.Data;

import java.util.Map;

@Data
public class ItemSaveDto {
    private Long id;
    private Map<String, String> name;
    private QualityType quality;
    private InventoryType inventoryType;
    private Long itemClassId;
    private Long itemSubclassId;
    private Integer level;
    private Map<String, Object> previewItem;
    private String iconUrl;
}
