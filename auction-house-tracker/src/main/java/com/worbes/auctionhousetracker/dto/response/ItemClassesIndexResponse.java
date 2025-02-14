package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemClassesIndexResponse {

    @JsonProperty("item_classes")
    private List<ItemClass> itemClasses;

    public ItemClassesIndexResponse() {
    }

    @Getter
    @Setter
    public static class ItemClass {
        private Long id;
        private Language name;

        public ItemClass(Long id, Language name) {
            this.id = id;
            this.name = name;
        }
    }
}
