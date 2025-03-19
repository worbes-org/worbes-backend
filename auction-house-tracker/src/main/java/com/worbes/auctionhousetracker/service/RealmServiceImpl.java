package com.worbes.auctionhousetracker.service;

import com.worbes.auctionhousetracker.dto.response.RealmIndexResponse;
import com.worbes.auctionhousetracker.entity.Realm;
import com.worbes.auctionhousetracker.entity.enums.Region;
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
    public List<String> getMissingRealmSlugs(RealmIndexResponse response, Region region) {
        List<Realm> realms = realmRepository.findByRegion(region);

        // DB에 저장된 Realm이 없으면, API에서 받은 모든 Realm Slug 반환
        if (realms.isEmpty()) {
            return response.getRealms()
                    .stream()
                    .map(RealmIndexResponse.RealmDto::getSlug) // ✅ Slug 반환
                    .toList();
        }

        // 기존 Realm Slug들을 Set으로 변환 (O(1) 조회)
        Set<String> existingRealmSlugs = realms.stream()
                .map(Realm::getSlug) // ✅ Slug 기반으로 비교
                .collect(Collectors.toSet());

        // DB에 없는 Realm Slug만 필터링하여 반환
        return response.getRealms()
                .stream()
                .map(RealmIndexResponse.RealmDto::getSlug) // ✅ Slug 반환
                .filter(slug -> !existingRealmSlugs.contains(slug))
                .toList();
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
