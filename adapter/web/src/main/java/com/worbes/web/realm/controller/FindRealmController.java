package com.worbes.web.realm.controller;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.port.in.FindRealmByRegionUseCase;
import com.worbes.web.common.model.ApiResponse;
import com.worbes.web.realm.model.FindRealmRequest;
import com.worbes.web.realm.model.FindRealmResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/realms",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class FindRealmController {

    private final FindRealmByRegionUseCase findRealmByRegionUseCase;

    @GetMapping
    public ApiResponse<List<FindRealmResponse>> findRealmByRegion(@Valid FindRealmRequest request) {
        List<Realm> realms = findRealmByRegionUseCase.findByRegion(request.region());
        List<FindRealmResponse> responses = realms.stream()
                .map(FindRealmResponse::new)
                .toList();

        return new ApiResponse<>(responses);
    }
}
