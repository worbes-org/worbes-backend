package com.worbes.infra.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

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
        private Map<String, String> name;

        public ItemClassDto(Long id, Map<String, String> name) {
            this.id = id;
            this.name = name;
        }
    }
}
