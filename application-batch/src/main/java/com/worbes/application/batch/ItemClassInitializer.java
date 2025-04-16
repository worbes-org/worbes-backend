package com.worbes.application.batch;

import com.worbes.domain.item.ItemClass;
import com.worbes.domain.shared.LocalizedName;
import com.worbes.domain.shared.RegionType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ItemClassInitializer implements DataInitializer {

    private final ItemClassService itemClassService;
    private final FetchItemClassPort fetcher;

    @Override
    public void init() {
        if (itemClassService.allRequiredClassesExist()) return;

        List<ItemClass> itemClasses = fetcher.fetchItemClassesIndex(RegionType.KR)
                .stream()
                .map(dto -> new ItemClass(dto.getId(), LocalizedName.fromRaw(dto.getName())))
                .toList();
        itemClassService.saveRequiredClasses(itemClasses);
    }
}
