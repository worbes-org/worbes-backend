package com.worbes.infra.rest.blizzard.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemClassesIndexResponse {

    @JsonProperty("item_classes")
    private List<ItemClassIndexElement> itemClassIndexElements;

    @Getter
    @Setter
    public static class ItemClassIndexElement {
        private Long id;
        private Map<String, String> name;

        public ItemClassIndexElement(Long id, Map<String, String> name) {
            this.id = id;
            this.name = name;
        }
    }
}
