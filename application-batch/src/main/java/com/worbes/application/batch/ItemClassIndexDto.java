package com.worbes.application.batch;

import lombok.Data;

import java.util.Map;

@Data
public class ItemClassIndexDto {
    private Long id;
    private Map<String, String> name;
}
  
