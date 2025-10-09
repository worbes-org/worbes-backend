package com.worbes.application.auction.service;

import com.worbes.application.auction.model.AuctionTrendPoint;
import com.worbes.application.auction.port.in.GetAuctionTrendQuery;
import com.worbes.application.auction.port.in.GetAuctionTrendResult;
import com.worbes.application.auction.port.out.FindAuctionTrendPort;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAuctionTrendServiceTest {

    @Mock
    FindAuctionTrendPort findAuctionTrendPort;

    @InjectMocks
    GetAuctionTrendService service;

    @Test
    @DisplayName("경매 트렌드 조회 결과가 정상적으로 반환된다")
    void execute_returnsCorrectTrendResult() {
        // given
        GetAuctionTrendQuery query = new GetAuctionTrendQuery(RegionType.KR, null, 210930L, null, 7);
        List<AuctionTrendPoint> trendPoints = List.of(
                new AuctionTrendPoint(1L, 101L, List.of(), Instant.now(), 1000L, 5),
                new AuctionTrendPoint(2L, 101L, List.of(), Instant.now(), 2000L, 3),
                new AuctionTrendPoint(3L, 101L, List.of(), Instant.now(), 3000L, 2)
        );
        when(findAuctionTrendPort.findTrendsBy(query)).thenReturn(trendPoints);

        // when
        GetAuctionTrendResult result = service.execute(query);

        // then
        assertThat(result.averageLowestPrice()).isEqualTo(2000); // (1000+2000+3000)/3
        assertThat(result.medianLowestPrice()).isEqualTo(2000);  // 중앙값
        assertThat(result.trendPoints()).isEqualTo(trendPoints);
    }

    @Test
    @DisplayName("빈 트렌드 데이터도 정상 처리된다")
    void execute_emptyTrend_returnsZeroStatistics() {
        // given
        GetAuctionTrendQuery query = new GetAuctionTrendQuery(RegionType.KR, null, 210930L, null, 7);
        when(findAuctionTrendPort.findTrendsBy(query)).thenReturn(List.of());

        // when
        GetAuctionTrendResult result = service.execute(query);

        // then
        assertThat(result.averageLowestPrice()).isEqualTo(0);
        assertThat(result.medianLowestPrice()).isEqualTo(0);
        assertThat(result.trendPoints()).isEmpty();
    }
}
