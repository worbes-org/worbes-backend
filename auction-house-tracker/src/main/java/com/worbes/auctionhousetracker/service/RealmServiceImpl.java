package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.builder.BlizzardApiParamsBuilder;
import com.worbes.auctionhousetracker.builder.BlizzardApiUrlBuilder;
import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
import com.worbes.auctionhousetracker.infrastructure.rest.RestApiClient;
import com.worbes.auctionhousetracker.repository.RealmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.worbes.auctionhousetracker.entity.enums.NamespaceType.DYNAMIC;

@Slf4j
@Service
public class RealmServiceImpl implements RealmService {

    private final RealmRepository realmRepository;
    private final RestApiClient restApiClient;
    private final ThreadPoolTaskExecutor taskExecutor;

    public RealmServiceImpl(RealmRepository realmRepository,
                            RestApiClient restApiClient,
                            @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.realmRepository = realmRepository;
        this.restApiClient = restApiClient;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public long count() {
        return realmRepository.count();
    }

    @Override
    public void saveAll(Iterable<Realm> realms) {
        realmRepository.saveAll(realms);
    }

    @Override
    public RealmIndexResponse fetchRealmIndex(Region region) {
        String path = BlizzardApiUrlBuilder.builder(region).realmIndex().build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();
        return restApiClient.get(path, params, RealmIndexResponse.class);
    }

    @Override
    public Realm fetchRealm(Region region, String slug) {
        String path = BlizzardApiUrlBuilder.builder(region).realm(slug).build();
        Map<String, String> params = BlizzardApiParamsBuilder.builder(region).namespace(DYNAMIC).build();
        RealmResponse realmResponse = restApiClient.get(path, params, RealmResponse.class);
        return Realm.builder()
                .id(realmResponse.getId())
                .region(region)
                .name(realmResponse.getName())
                .connectedRealmId(extractIdFromUrl(realmResponse.getConnectedRealmHref()))
                .build();
    }

    @Override
    public CompletableFuture<Realm> fetchRealmAsync(Region region, String slug) {
        return CompletableFuture.supplyAsync(() -> fetchRealm(region, slug), taskExecutor);
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

    @Override
    public boolean isRealmInitialized() {
        long count = realmRepository.count();
        log.info("현재 저장된 서버 수: {}", count);
        return count > 0;
    }

    @Override
    public CompletableFuture<Void> fetchAndSaveRealms(Region region) {
        log.info("Initializing realms for region: {}", region);
        // Realm 인덱스 조회
        RealmIndexResponse realmIndexResponse = fetchRealmIndex(region);

        // 각 Realm 정보 비동기로 조회
        List<CompletableFuture<Realm>> realmFutures = realmIndexResponse.getRealms().stream()
                .map(realm -> fetchRealmAsync(region, realm.getSlug()))
                .toList();

        // 모든 비동기 작업 완료 대기 후 저장
        return CompletableFuture.allOf(realmFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> realmFutures.stream().map(CompletableFuture::join).toList())
                .thenAccept(this::saveAll)
                .thenRun(() -> log.info("✅ [{}] - 모든 서버 데이터 저장 완료", region.name()));
    }

    @Override
    public List<Long> getConnectedRealmIdsByRegion(Region region) {
        return realmRepository.findDistinctConnectedRealmIdsByRegion(region);
    }
}
