package com.worbes.adapter.blizzard.data.realm;

import com.worbes.adapter.blizzard.client.BlizzardApiClient;
import com.worbes.adapter.blizzard.data.shared.BlizzardApiUriFactory;
import com.worbes.adapter.blizzard.data.shared.BlizzardResponseValidator;
import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.out.FetchRealmApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FetchRealmApiPortAdapter implements FetchRealmApiPort {

    private final BlizzardApiClient apiClient;
    private final BlizzardApiUriFactory uriFactory;
    private final BlizzardResponseValidator validator;

    @Override
    public Set<String> fetchRealmIndex(RegionType region) {
        URI uri = uriFactory.realmIndexUri(region);

        return apiClient.fetch(uri, RealmIndexResponse.class)
                .realms().stream()
                .map(validator::validate)
                .map(RealmResponse::slug)
                .collect(Collectors.toSet());
    }

    @Override
    public CompletableFuture<Realm> fetchAsync(RegionType region, String slug) {
        URI uri = uriFactory.realmUri(region, slug);

        return apiClient.fetchAsync(uri, RealmResponse.class)
                .thenApply(validator::validate)
                .thenApply(response -> new Realm(
                                response.id(),
                                extractConnectedRealmId(response.connectedRealmHref()),
                                region,
                                response.name(),
                                response.slug()
                        )
                );
    }

    private Long extractConnectedRealmId(String url) {
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
