package com.worbes.domain.item.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ItemClassDto {
    private Long id;
    private Map<String, String> name;
}
  
