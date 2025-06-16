package com.worbes.application.item.port.out;

import java.util.Map;

public record ItemSubclassFetchResult(
        Long id,
        Long classId,
        Map<String, String> displayName,
        Map<String, String> verboseName
) {
}
