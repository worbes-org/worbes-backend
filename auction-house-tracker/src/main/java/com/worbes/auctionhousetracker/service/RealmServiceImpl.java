package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.dto.response.RealmResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.RegionType;
import com.worbes.auctionhousetracker.repository.RealmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmServiceImpl implements RealmService {

    private final RealmRepository realmRepository;

    @Override
    public Realm get(RegionType region, Long realmId) {
        if (realmId == null) return null;
        return realmRepository.findByIdAndRegion(realmId, region).orElse(null);
    }

    @Override
    public List<Long> getConnectedRealmIds(RegionType region) {
        return realmRepository.findDistinctConnectedRealmIdsByRegion(region);
    }

    @Override
    public List<String> getMissingRealmSlugs(RealmIndexResponse response, RegionType region) {
        log.info("[{}] Missing Realm Slug 조회 시작", region.getValue());
        Set<String> existingRealmSlugs = realmRepository.findByRegion(region)
                .stream()
                .map(Realm::getSlug)
                .collect(Collectors.toSet());
        List<String> missingSlugs = response.getRealms()
                .stream()
                .map(RealmIndexResponse.RealmDto::getSlug)
                .filter(slug -> !existingRealmSlugs.contains(slug))
                .toList();
        log.info("[{}] DB에 없는 Realm 개수: {}", region.getValue(), missingSlugs.size());
        return missingSlugs;
    }

    @Override
    public void save(RegionType region, List<RealmResponse> responses) {
        if (responses.isEmpty()) {
            log.info("responses is empty");
            return;
        }

        log.info("[{}] {}개의 Realm 저장 시작", region.getValue(), responses.size());
        List<Realm> realms = responses.stream().map(response -> {
                    Long connectedRealmId = extractIdFromUrl(response.getConnectedRealmHref());
                    return Realm.builder()
                            .id(response.getId())
                            .region(region)
                            .name(response.getName())
                            .connectedRealmId(connectedRealmId)
                            .slug(response.getSlug())
                            .build();
                }
        ).toList();

        realmRepository.saveAll(realms);
        log.info("[{}] {}개의 Realm 저장 완료", region.getValue(), realms.size());
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
