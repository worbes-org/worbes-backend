package com.worbes.auctionhousetracker.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.config.properties.RestClientConfigProperties;
import com.worbes.auctionhousetracker.dto.response.ItemClassesIndexResponse;
import com.worbes.auctionhousetracker.entity.ItemClass;
import com.worbes.auctionhousetracker.entity.embeded.Language;
import com.worbes.auctionhousetracker.exception.BlizzardApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BlizzardRestClient.class)
class BlizzardRestClientTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    BlizzardRestClient blizzardRestClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestClientConfigProperties properties;

    @Test
    void shouldFindItemClassesIndex() throws JsonProcessingException {
        ItemClassesIndexResponse data = new ItemClassesIndexResponse();
        data.setItemClasses(List.of(
                new ItemClassesIndexResponse.ItemClass(1L, new Language("Sword", "Espada", "Espada", "Schwert", "Sword", "Espada", "Épée", "Spada", "Меч", "검", "劍", "剑")),
                new ItemClassesIndexResponse.ItemClass(2L, new Language("Axe", "Hacha", "Machado", "Axt", "Axe", "Hacha", "Hache", "Ascia", "Топор", "도끼", "斧", "斧"))
        ));

        server.expect(requestTo("https://kr.api.blizzard.com/item-class/index?namespace=static-kr&:region=kr&locale=kr"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(data), MediaType.APPLICATION_JSON));

        List<ItemClass> itemClasses = blizzardRestClient.fetchItemClassesIndex();
        assertEquals(2, itemClasses.size());
    }

    @Test
    void shouldThrowBlizzardApiExceptionWhen401ErrorOccurs() {
        // Given
        server.expect(requestTo("https://kr.api.blizzard.com/data/wow/item-class/index?namespace=static-kr&:region=kr&locale=kr"))
                .andRespond(withUnauthorizedRequest());

        // When & Then
        assertThatThrownBy(() -> blizzardRestClient.fetchItemClassesIndex())
                .isInstanceOf(BlizzardApiException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void shouldThrowBlizzardApiExceptionWhen429ErrorOccurs() {
        // Given
        server.expect(requestTo("https://kr.api.blizzard.com/item-class/index?namespace=static-kr&:region=kr&locale=kr"))
                .andRespond(withTooManyRequests());

        // When & Then
        assertThatThrownBy(() -> blizzardRestClient.fetchItemClassesIndex())
                .isInstanceOf(BlizzardApiException.class)
                .hasMessageContaining("Too Many Requests");
    }

    @TestConfiguration
    @EnableConfigurationProperties(RestClientConfigProperties.class)
    static class TestConfig {

        @Autowired
        RestClientConfigProperties properties;

        @Bean
        RestClient restClient(RestClient.Builder builder) {
            return builder.baseUrl(properties.getBaseUrl()).build();
        }
    }
}
