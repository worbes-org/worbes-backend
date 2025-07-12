package com.worbes.web.realm.controller;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.port.in.GetRealmUseCase;
import com.worbes.web.common.ApiResponse;
import com.worbes.web.realm.model.GetRealmRequest;
import com.worbes.web.realm.model.GetRealmResponse;
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
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GetRealmController {

    private final GetRealmUseCase getRealmUseCase;

    @GetMapping
    public ApiResponse<List<GetRealmResponse>> get(@Valid GetRealmRequest request) {
        List<Realm> realms = getRealmUseCase.getAll(request.region());
        List<GetRealmResponse> responses = realms.stream()
                .map(GetRealmResponse::new)
                .toList();

        return new ApiResponse<>(responses);
    }
}
