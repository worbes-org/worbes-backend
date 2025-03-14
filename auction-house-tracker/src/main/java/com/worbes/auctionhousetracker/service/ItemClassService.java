package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.embeded.Translation;
import com.worbes.auctionhousetracker.entity.enums.LocaleType;
import com.worbes.auctionhousetracker.repository.ItemClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemClassService {

    private static final Set<Long> REQUIRED_ITEM_CLASSES = Set.of(
            0L,  // 소비용품 (Consumable)
            1L,  // 가방류 (Container)
            2L,  // 무기 (Weapon)
            3L,  // 보석 (Gem)
            4L,  // 방어구 (Armor)
            5L,  // 재료 (Reagent)
            8L,  // 아이템 강화 (Item Enhancement)
            9L,  // 제조법 (Recipe)
            12L, // 퀘스트 아이템 (Quest)
            15L, // 기타 (Miscellaneous)
            16L, // 문양 (Glyph)
            17L, // 전투 애완동물 (Battle Pets)
            18L, // WoW 토큰 (WoW Token)
            19L  // 전문 기술 장비 (Profession)
    );

    private final ItemClassRepository itemClassRepository;

    public void convertAndSaveItemClasses(ItemClassesIndexResponse response) {
        List<ItemClass> itemClasses = response.getItemClassDtos().stream()
                .filter(dto -> isRequiredItemClass(dto.getId()))
                .map(this::convertToItemClasses)
                .flatMap(List::stream)
                .toList();

        itemClassRepository.saveAll(itemClasses);
    }

    private List<ItemClass> convertToItemClasses(ItemClassesIndexResponse.ItemClassDto dto) {
        List<ItemClass> itemClasses = new ArrayList<>();

        for (LocaleType locale : LocaleType.values()) {
            String name = extractLocalizedName(dto.getName(), locale);
            ItemClass itemClass = ItemClass.builder()
                    .itemClassId(dto.getId())
                    .locale(locale.getCode())
                    .name(name)
                    .build();
            itemClasses.add(itemClass);
        }

        return itemClasses;
    }

    private String extractLocalizedName(Translation name, LocaleType locale) {
        return switch (locale) {
            case EN_US -> name.getEn_US();
            case KO_KR -> name.getKo_KR();
            case FR_FR -> name.getFr_FR();
            case DE_DE -> name.getDe_DE();
            case ZH_CN -> name.getZh_CN();
            case ZH_TW -> name.getZh_TW();
            case ES_ES -> name.getEs_ES();
            case ES_MX -> name.getEs_MX();
            case IT_IT -> name.getIt_IT();
            case RU_RU -> name.getRu_RU();
            case PT_BR -> name.getPt_BR();
        };
    }

    public boolean isItemClassStored(Long itemClassId, String locale) {
        return itemClassRepository.existsByItemClassIdAndLocale(itemClassId, locale);
    }

    public boolean isRequiredItemClass(Long itemClassId) {
        return REQUIRED_ITEM_CLASSES.contains(itemClassId);
    }

    public Set<Long> getRequiredItemClasses() {
        return REQUIRED_ITEM_CLASSES;
    }
}
