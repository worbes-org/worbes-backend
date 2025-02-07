package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.entity.enums.BindingType;
import com.worbes.auctionhousetracker.entity.enums.InventoryType;
import com.worbes.auctionhousetracker.entity.enums.QualityType;
import lombok.Data;

import java.util.Map;

@Data
public class ItemResponse {

    @JsonProperty("preview_item")
    private PreviewItem previewItem;

    @Data
    private static class PreviewItem {

        private Long id;
        private QualityType qualityType;
        private Language name;
        private String mediaHref;
        private Long itemClassId;
        private Long itemSubclassId;
        private InventoryType inventoryType;
        private Long sellPrice;
        private Language description;
        @JsonProperty("is_subclass_hidden")
        private Boolean isSubclassHidden;
        @JsonProperty("crafting_reagent")
        private Language craftingReagent;
        private BindingType bindingType;

        @JsonProperty("item")
        private void mapItemId(Map<String, Object> item) {
            id = (Long) item.get("id");
        }

        @JsonProperty("quality")
        private void mapQualityType(Map<String, Object> type) {
            qualityType = QualityType.valueOf((String) type.get("type"));
        }

        @JsonProperty("media")
        private void mapMediaHref(MediaDto mediaDto) {
            mediaHref = mediaDto.getKey().getHref();
        }

        @JsonProperty("item_class")
        private void mapItemClassId(Map<String, Object> map) {
            itemClassId = (Long) map.get("id");
        }

        @JsonProperty("item_subclass")
        private void mapItemSubclassId(Map<String, Object> map) {
            itemSubclassId = (Long) map.get("id");
        }

        @JsonProperty("inventory_type")
        private void mapInventoryType(Map<String, Object> map) {
            inventoryType = InventoryType.valueOf((String) map.get("type"));
        }

        @JsonProperty("sell_price")
        private void mapSellPrice(Map<String, Object> map) {
            sellPrice = (Long) map.get("value");
        }

        @JsonProperty("binding")
        private void mapBindType(Map<String, Object> map) {
            bindingType = BindingType.valueOf((String) map.get("type"));
        }
    }

    @Data
    public static class MediaDto {
        private KeyDto key;
        private Long id;
    }

    @Data
    public static class KeyDto {
        private String href;
    }
}
