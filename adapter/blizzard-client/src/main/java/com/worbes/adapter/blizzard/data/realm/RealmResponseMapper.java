package com.worbes.adapter.blizzard.data.realm;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

import java.net.MalformedURLException;
import java.net.URL;

@Primary
@Mapper(componentModel = "spring")
public interface RealmResponseMapper {

    @Mapping(target = "id", source = "response.id")
    @Mapping(target = "name", source = "response.name")
    @Mapping(target = "connectedRealmId", source = "response.connectedRealmHref")
    @Mapping(target = "slug", source = "response.slug")
    @Mapping(target = "region", source = "region")
    Realm toDomain(RealmResponse response, RegionType region);

    default Long connectedRealmHref(String url) {
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
}
