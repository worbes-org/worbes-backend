package com.worbes.application.realm.service;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.port.out.RealmFetchResult;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class RealmFactory {

    public Realm create(RealmFetchResult dto) {
        return Realm.builder()
                .id(dto.id())
                .connectedRealmId(extractIdFromUrl(dto.connectedRealmHref()))
                .region(dto.region())
                .name(dto.name())
                .slug(dto.slug())
                .build();
    }

    private Long extractIdFromUrl(String url) {
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
