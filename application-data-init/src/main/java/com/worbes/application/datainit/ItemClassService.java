package com.worbes.application.datainit;

import com.worbes.domain.item.ItemClass;
import com.worbes.domain.item.policy.RequiredItemClassPolicy;
import com.worbes.domain.item.port.ItemClassRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ItemClassService {

    private final RequiredItemClassPolicy policy;
    private final ItemClassRepository repository;

    public boolean allRequiredClassesExist() {
        Set<Long> required = policy.getRequiredIds();
        Set<Long> existing = repository.findAllBy(required).stream()
                .map(ItemClass::getId)
                .collect(Collectors.toSet());

        Set<Long> missing = new HashSet<>(required);
        missing.removeAll(existing);

        return missing.isEmpty();
    }

    public void saveRequiredClasses(List<ItemClass> candidates) {
        Set<Long> required = policy.getRequiredIds();
        List<ItemClass> filtered = candidates.stream()
                .filter(it -> required.contains(it.getId()))
                .toList();

        repository.saveAll(filtered);
    }
}
