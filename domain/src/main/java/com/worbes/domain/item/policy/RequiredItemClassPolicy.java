package com.worbes.domain.item.policy;

import java.util.Set;

public interface RequiredItemClassPolicy {
    boolean isRequired(Long itemClassId);

    Set<Long> getRequiredIds();
}
