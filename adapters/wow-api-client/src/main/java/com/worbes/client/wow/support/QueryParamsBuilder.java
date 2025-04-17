package com.worbes.client.wow.support;

import com.worbes.domain.shared.RegionType;

import java.util.HashMap;
import java.util.Map;

public class QueryParamsBuilder {

    private static final String NAMESPACE = "namespace";
    private static final String REGION = ":region";

    private final Map<String, String> params = new HashMap<>();
    private final RegionType region;

    private QueryParamsBuilder(RegionType region) {
        if (region == null) {
            throw new IllegalArgumentException("Region은 필수값입니다.");
        }
        this.region = region;
        params.put(REGION, region.getValue());
    }

    public static QueryParamsBuilder builder(RegionType region) {
        return new QueryParamsBuilder(region);
    }

    public QueryParamsBuilder namespace(NamespaceType type) {
        params.put(NAMESPACE, type.format(region));
        return this;
    }

    public QueryParamsBuilder staticNamespace() {
        params.put(NAMESPACE, NamespaceType.STATIC.format(region));
        return this;
    }

    public QueryParamsBuilder dynamicNamespace() {
        params.put(NAMESPACE, NamespaceType.DYNAMIC.format(region));
        return this;
    }

    public Map<String, String> build() {
        checkNamespaceIsSet();
        return new HashMap<>(params);
    }

    private void checkNamespaceIsSet() {
        if (!params.containsKey(NAMESPACE)) {
            throw new IllegalStateException("Namespace가 설정되지 않았습니다. 먼저 namespace()를 호출하세요.");
        }
    }
}
