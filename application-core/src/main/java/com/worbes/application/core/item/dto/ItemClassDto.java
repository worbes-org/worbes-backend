package com.worbes.application.core.item.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ItemClassDto {
    private Long id;
    private Map<String, String> name;
}
  
