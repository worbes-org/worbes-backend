package com.worbes.infra.rest.blizzard.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemClassesIndexResponse {

    @JsonProperty("item_classes")
    private List<ItemClass> itemClasses;

    @Getter
    @Setter
    public static class ItemClass {
        private Long id;
        private Map<String, String> name;

        public ItemClass(Long id, Map<String, String> name) {
            this.id = id;
            this.name = name;
        }
    }
}
