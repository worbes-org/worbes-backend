package com.worbes.application.datainit;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ItemClassDto {

    private Long id;
    private Map<String, String> name;
    private List<ItemSubclass> subclassResponses;

    @Data
    public static class ItemSubclass {
        private Long id;
        private Map<String, String> name;
    }
}
