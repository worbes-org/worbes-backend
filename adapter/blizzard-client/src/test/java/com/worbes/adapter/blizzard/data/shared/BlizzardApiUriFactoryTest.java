package com.worbes.adapter.blizzard.data.shared;

import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class BlizzardApiUriFactoryTest {

    private static final RegionType REGION = RegionType.KR;
    private static final String BASE_URL = "https://kr.api.blizzard.com";
    private final BlizzardApiUriFactory uriFactory = new BlizzardApiUriFactory();

    @Test
    @DisplayName("auctionUri는 realmId가 포함된 URI를 반환해야 한다")
    void auctionUri_shouldContainRealmId() {
        Long realmId = 123L;

        URI uri = uriFactory.auctionUri(REGION, realmId);

        assertThat(uri.toString()).startsWith(BASE_URL + "/data/wow/connected-realm/123/auctions");
        assertThat(uri.getQuery()).contains("namespace=dynamic-kr", ":region=kr");
    }

    @Test
    @DisplayName("commodityUri는 고정된 경로를 반환해야 한다")
    void commodityUri_shouldContainCommodityPath() {
        URI uri = uriFactory.commodityUri(REGION);

        assertThat(uri.toString()).startsWith(BASE_URL + "/data/wow/auctions/commodities");
        assertThat(uri.getQuery()).contains("namespace=dynamic-kr", ":region=kr");
    }

    @Test
    @DisplayName("realmIndexUri는 정적 namespace와 함께 realm index URI를 반환해야 한다")
    void realmIndexUri_shouldBeCorrect() {
        URI uri = uriFactory.realmIndexUri(REGION);

        assertThat(uri.toString()).startsWith(BASE_URL + "/data/wow/realm/index");
        assertThat(uri.getQuery()).contains("namespace=dynamic-kr", ":region=kr");
    }

    @Test
    @DisplayName("realmUri는 slug를 포함한 URI를 반환해야 한다")
    void realmUri_shouldContainSlug() {
        URI uri = uriFactory.realmUri(REGION, "azshara");

        assertThat(uri.toString()).startsWith(BASE_URL + "/data/wow/realm/azshara");
        assertThat(uri.getQuery()).contains("namespace=dynamic-kr", ":region=kr");
    }

    @Test
    @DisplayName("itemUri는 itemId에 따른 경로와 쿼리 파라미터를 포함해야 한다")
    void itemUri_shouldContainItemId() {
        URI uri = uriFactory.itemUri(1001L);

        assertThat(uri.toString()).startsWith("https://us.api.blizzard.com/data/wow/item/1001");
        assertThat(uri.getQuery()).contains("namespace=static-us", ":region=us");
    }

    @Test
    @DisplayName("itemClassesIndexUri는 정적 경로를 포함한 URI를 반환해야 한다")
    void itemClassesIndexUri_shouldReturnCorrectPath() {
        URI uri = uriFactory.itemClassesIndexUri();

        assertThat(uri.toString()).startsWith("https://us.api.blizzard.com/data/wow/item-class/index");
        assertThat(uri.getQuery()).contains("namespace=static-us", ":region=us");
    }

    @Test
    @DisplayName("itemSubclassUri는 itemClassId와 subclassId를 포함한 URI를 반환해야 한다")
    void itemSubclassUri_shouldContainIds() {
        URI uri = uriFactory.itemSubclassUri(1L, 2L);

        assertThat(uri.toString()).startsWith("https://us.api.blizzard.com/data/wow/item-class/1/item-subclass/2");
        assertThat(uri.getQuery()).contains("namespace=static-us", ":region=us");
    }

    @Test
    @DisplayName("mediaUri는 itemId를 포함한 URI를 반환해야 한다")
    void mediaUri_shouldContainItemId() {
        URI uri = uriFactory.mediaUri(999L);

        assertThat(uri.toString()).startsWith("https://us.api.blizzard.com/data/wow/media/item/999");
        assertThat(uri.getQuery()).contains("namespace=static-us", ":region=us");
    }
}
