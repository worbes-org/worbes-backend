package com.worbes.auctionhousetracker.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemClassesIndexResponse {

    @JsonProperty("item_classes")
    private List<ItemClassDto> itemClassDtos;

    @Getter
    @Setter
    public static class ItemClassDto {
        private Long id;
        private Language name;

        public ItemClassDto(Long id, Language name) {
            this.id = id;
            this.name = name;
        }
    }
}
