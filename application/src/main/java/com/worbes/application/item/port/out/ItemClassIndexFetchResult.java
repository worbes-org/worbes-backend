package com.worbes.application.item.port.out;

import java.util.Map;

public record ItemClassIndexFetchResult(
        Long id,
        Map<String, String> name
) {
}
  
