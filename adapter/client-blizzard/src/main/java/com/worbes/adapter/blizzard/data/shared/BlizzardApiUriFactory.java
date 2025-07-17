package com.worbes.adapter.blizzard.data.shared;

import com.worbes.application.realm.model.RegionType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class BlizzardApiUriFactory {

    private static final String BASE_URL_FORMAT = "https://%s.api.blizzard.com";
    private static final String ITEM_CLASSES_INDEX_PATH = "/data/wow/item-class/index";
    private static final String ITEM_SUBCLASS_PATH_FORMAT = "/data/wow/item-class/%s/item-subclass/%s";
    private static final String ITEM_PATH_FORMAT = "/data/wow/item/%s";
    private static final String MEDIA_PATH_FORMAT = "/data/wow/media/item/%s";
    private static final String REALM_INDEX_PATH = "/data/wow/realm/index";
    private static final String REALM_PATH_FORMAT = "/data/wow/realm/%s";
    private static final String AUCTION_PATH_FORMAT = "/data/wow/connected-realm/%s/auctions";
    private static final String COMMODITY_PATH = "/data/wow/auctions/commodities";
    private static final RegionType DEFAULT_REGION = RegionType.US;

    private RegionType effectiveRegion(RegionType region) {
        return region != null ? region : DEFAULT_REGION;
    }

    private String baseUrl(RegionType region) {
        return String.format(BASE_URL_FORMAT, effectiveRegion(region).getValue());
    }

    private Map<String, String> queryParams(NamespaceType namespaceType, RegionType region) {
        RegionType effective = effectiveRegion(region);
        Map<String, String> params = new HashMap<>();
        params.put("namespace", namespaceType.format(effective));
        params.put(":region", effective.getValue());
        return params;
    }

    private URI buildUri(String url, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (!queryParams.isEmpty()) {
            queryParams.forEach(builder::queryParam);
        }
        return builder.build().toUri();
    }

    public URI auctionUri(RegionType region, Long realmId) {
        return buildUri(
                baseUrl(region) + String.format(AUCTION_PATH_FORMAT, realmId),
                queryParams(NamespaceType.DYNAMIC, region)
        );
    }

    public URI commodityUri(RegionType region) {
        return buildUri(
                baseUrl(region) + COMMODITY_PATH,
                queryParams(NamespaceType.DYNAMIC, region)
        );
    }

    public URI realmIndexUri(RegionType region) {
        return buildUri(
                baseUrl(region) + REALM_INDEX_PATH,
                queryParams(NamespaceType.DYNAMIC, region)
        );
    }

    public URI realmUri(RegionType region, String slug) {
        return buildUri(
                baseUrl(region) + String.format(REALM_PATH_FORMAT, slug),
                queryParams(NamespaceType.DYNAMIC, region)
        );
    }

    public URI itemUri(Long itemId) {
        return buildUri(
                baseUrl(DEFAULT_REGION) + String.format(ITEM_PATH_FORMAT, itemId),
                queryParams(NamespaceType.STATIC, DEFAULT_REGION)
        );
    }

    public URI itemClassesIndexUri() {
        return buildUri(
                baseUrl(DEFAULT_REGION) + ITEM_CLASSES_INDEX_PATH,
                queryParams(NamespaceType.STATIC, DEFAULT_REGION)
        );
    }

    public URI itemSubclassUri(Long itemClassId, Long subclassId) {
        return buildUri(
                baseUrl(DEFAULT_REGION) + String.format(ITEM_SUBCLASS_PATH_FORMAT, itemClassId, subclassId),
                queryParams(NamespaceType.STATIC, DEFAULT_REGION)
        );
    }

    public URI mediaUri(Long itemId) {
        return buildUri(
                baseUrl(DEFAULT_REGION) + String.format(MEDIA_PATH_FORMAT, itemId),
                queryParams(NamespaceType.STATIC, DEFAULT_REGION)
        );
    }
}
