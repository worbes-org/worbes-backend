package com.worbes.application.item.port.in;

import java.util.Set;

public interface CreateItemUseCase {
    void execute(Set<Long> itemIds);
}
