package com.worbes.web;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.port.in.FindRealmByRegionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/realms")
@RequiredArgsConstructor
public class FindRealmController {

    private final FindRealmByRegionUseCase findRealmByRegionUseCase;

    @GetMapping
    public ApiResponse<List<FindRealmResponse>> findRealmByRegion(FindRealmRequest request) {
        log.info("[FindRealmController] 요청 수신 - region: {}", request.region());
        List<Realm> realms = findRealmByRegionUseCase.findByRegion(request.region());
        List<FindRealmResponse> responses = realms.stream().map(FindRealmResponse::new).toList();
        log.info("[FindRealmController] 응답 생성 완료 - region: {}, realms: {}", request.region(), responses.size());

        return new ApiResponse<>(responses);
    }
}
