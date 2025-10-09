package com.worbes.application.auction.model;

import com.worbes.application.item.model.InventoryType;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.model.QualityType;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuctionSnapshotTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1001L)
                .name(Map.of("en_US", "Sword", "ko_KR", "검"))
                .classId(2L)
                .subclassId(3L)
                .quality(QualityType.EPIC)
                .level(50)
                .inventoryType(InventoryType.WEAPON)
                .isStackable(true)
                .craftingTier(1)
                .icon("sword_icon")
                .expansionId(9)
                .displayId(2001L)
                .build();
    }

    @Test
    void getItemLevel_baseAndBonusNull_returnsItemLevel() {
        AuctionSnapshot snapshot = new AuctionSnapshot(
                Instant.now(),
                item,
                RegionType.KR,
                1L,
                10,
                1000L,
                null,
                null,
                null,
                "suffix"
        );

        assertThat(snapshot.getItemLevel()).isEqualTo(50);
    }

    @Test
    void getItemLevel_baseNotNull_bonusNull_returnsBase() {
        AuctionSnapshot snapshot = new AuctionSnapshot(
                Instant.now(),
                item,
                RegionType.KR,
                1L,
                10,
                1000L,
                null,
                null,
                70,
                "suffix"
        );

        assertThat(snapshot.getItemLevel()).isEqualTo(70);
    }

    @Test
    void getItemLevel_baseNull_bonusNotNull_returnsItemPlusBonus() {
        AuctionSnapshot snapshot = new AuctionSnapshot(
                Instant.now(),
                item,
                RegionType.KR,
                1L,
                10,
                1000L,
                null,
                5,
                null,
                "suffix"
        );

        assertThat(snapshot.getItemLevel()).isEqualTo(55);
    }

    @Test
    void getItemLevel_baseAndBonusNotNull_returnsSum() {
        AuctionSnapshot snapshot = new AuctionSnapshot(
                Instant.now(),
                item,
                RegionType.KR,
                1L,
                10,
                1000L,
                null,
                5,
                70,
                "suffix"
        );

        assertThat(snapshot.getItemLevel()).isEqualTo(75);
    }
}
