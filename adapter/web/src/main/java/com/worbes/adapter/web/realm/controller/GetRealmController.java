package com.worbes.adapter.web.realm.controller;

import com.worbes.adapter.web.common.ApiResponse;
import com.worbes.adapter.web.realm.model.GetRealmResponse;
import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.model.RegionType;
import com.worbes.application.realm.port.in.GetRealmUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/realms",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GetRealmController {

    private final GetRealmUseCase getRealmUseCase;

    @GetMapping
    public ApiResponse<List<GetRealmResponse>> get(
            @RequestParam("region") RegionType region
    ) {
        List<Realm> realms = getRealmUseCase.execute(region);
        List<GetRealmResponse> responses = realms.stream()
                .map(GetRealmResponse::new)
                .toList();

        return new ApiResponse<>(responses);
    }
}
