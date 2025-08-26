package com.worbes.api;

import com.worbes.adapter.web.auction.model.SearchAuctionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(scripts = "/sql/find_auction_snapshot_test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class SearchAuctionControllerSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auctions";
    }


    private ResponseEntity<SliceResponse<SearchAuctionResponse>> request(
            String name, Long classId, Long subclassId,
            Integer minQuality, Integer maxQuality,
            Integer minItemLevel, Integer maxItemLevel,
            Integer expansionId
    ) {
        String url = baseUrl() + "?region=KR&realmId=1" +
                (name != null ? "&name=" + name : "") +
                (classId != null ? "&classId=" + classId : "") +
                (subclassId != null ? "&subclassId=" + subclassId : "") +
                (minQuality != null ? "&minQuality=" + minQuality : "") +
                (maxQuality != null ? "&maxQuality=" + maxQuality : "") +
                (minItemLevel != null ? "&minItemLevel=" + minItemLevel : "") +
                (maxItemLevel != null ? "&maxItemLevel=" + maxItemLevel : "") +
                (expansionId != null ? "&expansionId=" + expansionId : "");

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Test
    @DisplayName("이름 필터 적용 시 전체 응답 검증")
    void testNameFilter_fullResponse() {
        var res = request("닻풀", null, null, null, null, null, null, null);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();

        SearchAuctionResponse auction = res.getBody().content().get(0);
        assertThat(auction.itemId()).isEqualTo(1003L);
        assertThat(auction.itemBonus()).isNull();
        assertThat(auction.itemLevel()).isEqualTo(45);
        assertThat(auction.craftingTier()).isEqualTo(1);
        assertThat(auction.lowestPrice()).isEqualTo(30000L);
        assertThat(auction.totalQuantity()).isEqualTo(11035);
    }

    @Test
    @DisplayName("모든 필터 적용 시 전체 응답 검증")
    void testAllFiltersTogether_fullResponse() {
        var res = request("테스트 검", 2L, 7L, 1, 5, 200, 250, 5);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().content()).hasSize(1);

        SearchAuctionResponse auction = res.getBody().content().get(0);
        assertThat(auction.itemId()).isEqualTo(1001L);
        assertThat(auction.itemBonus()).isEqualTo("9001:9003");
        assertThat(auction.itemLevel()).isEqualTo(235);
        assertThat(auction.craftingTier()).isNull();
        assertThat(auction.lowestPrice()).isEqualTo(150000L);
        assertThat(auction.totalQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("classId 필터 적용 시 응답 전체 검증")
    void testClassIdFilter_fullResponse() {
        var res = request(null, 4L, null, null, null, null, null, null);
        assertThat(res.getBody()).isNotNull();
        var auctions = res.getBody().content();

        assertThat(auctions).hasSize(2);
        auctions.forEach(a -> assertThat(a.itemId()).isIn(1002L, 1003L));
    }

    @Test
    @DisplayName("classId + subclassId 필터 적용 시 응답 전체 검증")
    void testSubclassIdFilter_fullResponse() {
        var res = request(null, 2L, 7L, null, null, null, null, null);
        assertThat(res.getBody()).isNotNull();
        var auction = res.getBody().content().get(0);

        assertThat(auction.itemId()).isEqualTo(1001L);
        assertThat(auction.itemBonus()).isEqualTo("9001:9003");
        assertThat(auction.itemLevel()).isEqualTo(235);
        assertThat(auction.craftingTier()).isNull();
        assertThat(auction.lowestPrice()).isEqualTo(150000L);
        assertThat(auction.totalQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("minQuality 필터 적용 시 응답 전체 검증")
    void testMinQualityFilter_fullResponse() {
        var res = request(null, null, null, 4, null, null, null, null);
        assertThat(res.getBody()).isNotNull();
        var auction = res.getBody().content().get(0);
        assertThat(auction.itemId()).isEqualTo(1001L);
    }

    @Test
    @DisplayName("maxQuality 필터 적용 시 응답 전체 검증")
    void testMaxQualityFilter_fullResponse() {
        var res = request(null, null, null, null, 3, null, null, null);
        assertThat(res.getBody()).isNotNull();
        var auctions = res.getBody().content();
        assertThat(auctions).extracting(SearchAuctionResponse::itemId)
                .containsExactlyInAnyOrder(1002L, 1003L);
    }

    @Test
    @DisplayName("minItemLevel 필터 적용 시 응답 전체 검증")
    void testMinItemLevelFilter_fullResponse() {
        var res = request(null, null, null, null, null, 100, null, null);
        assertThat(res.getBody()).isNotNull();
        var auctions = res.getBody().content();
        assertThat(auctions).extracting(SearchAuctionResponse::itemId)
                .containsExactlyInAnyOrder(1001L, 1002L);
    }

    @Test
    @DisplayName("maxItemLevel 필터 적용 시 응답 전체 검증")
    void testMaxItemLevelFilter_fullResponse() {
        var res = request(null, null, null, null, null, null, 150, null);
        assertThat(res.getBody()).isNotNull();
        var auctions = res.getBody().content();
        assertThat(auctions).extracting(SearchAuctionResponse::itemId)
                .containsExactlyInAnyOrder(1002L, 1003L);
    }

    @Test
    @DisplayName("expansionId 필터 적용 시 응답 전체 검증")
    void testExpansionIdFilter_fullResponse() {
        var res = request(null, null, null, null, null, null, null, 9);
        assertThat(res.getBody()).isNotNull();
        var auction = res.getBody().content().get(0);

        assertThat(auction.itemId()).isEqualTo(1003L);
        assertThat(auction.itemBonus()).isNull();
        assertThat(auction.itemLevel()).isEqualTo(45);
        assertThat(auction.craftingTier()).isEqualTo(1);
        assertThat(auction.lowestPrice()).isEqualTo(30000L);
        assertThat(auction.totalQuantity()).isEqualTo(11035);
    }

    private record SliceResponse<T>(
            List<T> content,
            boolean first,
            boolean last,
            int number,
            int size,
            int numberOfElements,
            boolean empty
    ) {
    }
}
