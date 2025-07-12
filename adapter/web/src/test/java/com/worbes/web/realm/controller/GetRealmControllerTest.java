package com.worbes.web.realm.controller;

import com.worbes.application.realm.model.Realm;
import com.worbes.application.realm.port.in.GetRealmUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GetRealmController.class)
class GetRealmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetRealmUseCase getRealmUseCase;

    @Test
    @DisplayName("유효한 region 파라미터로 GET /api/v1/realms 호출 시 200 OK와 다국어 이름을 포함한 결과를 반환한다")
    void returnsOkWithValidRegionParameter() throws Exception {
        // given
        given(getRealmUseCase.getAll(any()))
                .willReturn(List.of(Realm.builder()
                                .id(1L)
                                .connectedRealmId(2116L)
                                .name(Map.of(
                                        "ko_KR", "아즈샤라",
                                        "en_US", "Azshara"
                                ))
                                .build()
                        )
                );

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/realms")
                .param("region", "KR")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].connectedRealmId").value(2116L))
                .andExpect(jsonPath("$.content[0].name.ko_KR").value("아즈샤라"))
                .andExpect(jsonPath("$.content[0].name.en_US").value("Azshara"));
    }

    @Test
    @DisplayName("여러 Realm 결과를 반환할 때 200 OK와 리스트를 반환한다")
    void returnsOkWithMultipleResults() throws Exception {
        // given
        given(getRealmUseCase.getAll(any()))
                .willReturn(List.of(
                        Realm.builder()
                                .id(1L)
                                .connectedRealmId(2116L)
                                .name(Map.of("ko_KR", "아즈샤라"))
                                .build(),
                        Realm.builder()
                                .id(2L)
                                .connectedRealmId(2117L)
                                .name(Map.of("ko_KR", "헬스크림"))
                                .build()
                ));

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/realms")
                .param("region", "KR")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].connectedRealmId").value(2116L))
                .andExpect(jsonPath("$.content[0].name.ko_KR").value("아즈샤라"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].connectedRealmId").value(2117L))
                .andExpect(jsonPath("$.content[1].name.ko_KR").value("헬스크림"));
    }

    @Test
    @DisplayName("region 파라미터가 없을 때 400 Bad Request를 반환한다")
    void returnsBadRequestWhenRegionIsMissing() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/realms")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("region 파라미터가 잘못된 값이면 400 Bad Request를 반환한다")
    void returnsBadRequestWhenRegionIsInvalidEnum() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/realms")
                .param("region", "INVALID")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }
}
