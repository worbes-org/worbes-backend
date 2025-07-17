package com.worbes.application.realm.model;

import com.worbes.application.common.model.LocalizedName;
import com.worbes.application.realm.port.out.FetchRealmApiResult;
import lombok.Builder;
import lombok.Getter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

@Getter
public class Realm {

    private final Long id;
    private final Long connectedRealmId;
    private final RegionType region;
    private final LocalizedName name;
    private final String slug;

    @Builder
    public Realm(Long id, Long connectedRealmId, RegionType region, Map<String, String> name, String slug) {
        this.id = id;
        this.connectedRealmId = connectedRealmId;
        this.region = region;
        this.name = LocalizedName.fromRaw(name);
        this.slug = slug;
    }

    public static Realm from(FetchRealmApiResult dto) {
        return Realm.builder()
                .id(dto.id())
                .connectedRealmId(extractConnectedRealmId(dto.connectedRealmHref()))
                .region(dto.region())
                .name(dto.name())
                .slug(dto.slug())
                .build();
    }

    private static Long extractConnectedRealmId(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();
            // 경로에 슬래시로 끝나는 경우 제거
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            String[] pathSegments = path.split("/");
            String lastSegment = pathSegments[pathSegments.length - 1];
            return Long.parseLong(lastSegment);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + url, e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format in URL: " + url, e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Realm realm = (Realm) o;
        return Objects.equals(id, realm.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return slug;
    }

    public Map<String, String> getName() {
        return name.asRaw();
    }
}
