package com.worbes.adapter.jpa.repository;

import com.worbes.application.auction.model.AuctionHistory;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(
        scripts = "find-auction-history-test.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = "auction-cleanup.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
public class FindAuctionHistoryTest {

    @Autowired
    private SearchAuctionRepository auctionRepository;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 7, 3, 0, 0, 0); // 7월 3일 오후 3시 기준
    }

    @DisplayName("itemId, region, realmId 조건 + 최근 7일 데이터만 시간 단위로 그룹화하여 quantity 합계와 최소 가격을 반환한다")
    @Test
    void findHourlySummary_shouldGroupByHourAndAggregate() {
        //given
        Long itemId = 100L;
        RegionType region = RegionType.KR;
        Long realmId = 1L;

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        assertThat(result).isNotEmpty();

        // 7월 3일 0시 기준 이전 7일 데이터 확인 (6월 26일 ~ 7월 3일)
        // itemId 100인 데이터는 다음 시간대에 존재:
        // - 6월 26일 09:00 (quantity: 10, unit_price: 200)
        // - 6월 26일 14:00 (quantity: 15, unit_price: 180)
        // - 6월 27일 10:00 (quantity: 8, unit_price: 220)
        // - 6월 27일 16:00 (quantity: 12, unit_price: 190)
        // - 6월 28일 11:00 (quantity: 20, unit_price: 250)
        // - 6월 28일 18:00 (quantity: 5, unit_price: 210)
        // - 6월 29일 12:00 (quantity: 18, unit_price: 230)
        // - 6월 29일 19:00 (quantity: 7, unit_price: 195)
        // - 6월 30일 13:00 (quantity: 25, unit_price: 280)
        // - 6월 30일 20:00 (quantity: 11, unit_price: 205)

        assertThat(result).hasSize(10);

        // 시간순으로 정렬되어 있는지 확인
        for (int i = 0; i < result.size() - 1; i++) {
            assertThat(result.get(i).getTime()).isBefore(result.get(i + 1).getTime());
        }

        // 첫 번째 데이터 확인 (6월 26일 09:00)
        AuctionHistory firstHistory = result.get(0);
        assertThat(firstHistory.getTime()).isEqualTo(LocalDateTime.of(2025, 6, 26, 9, 0, 0));
        assertThat(firstHistory.getTotalQuantity()).isEqualTo(10L);
        assertThat(firstHistory.getMinPrice()).isEqualTo(200L);

        // 마지막 데이터 확인 (6월 30일 20:00)
        AuctionHistory lastHistory = result.get(result.size() - 1);
        assertThat(lastHistory.getTime()).isEqualTo(LocalDateTime.of(2025, 6, 30, 20, 0, 0));
        assertThat(lastHistory.getTotalQuantity()).isEqualTo(11L);
        assertThat(lastHistory.getMinPrice()).isEqualTo(205L);
    }

    @DisplayName("존재하지 않는 itemId로 검색하면 빈 리스트를 반환한다")
    @Test
    void findHourlySummary_shouldReturnEmptyListForNonExistentItem() {
        // given
        Long itemId = 999L; // 존재하지 않는 itemId
        RegionType region = RegionType.KR;
        Long realmId = 1L;

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("다른 realmId로 검색하면 해당 realm의 데이터만 반환한다")
    @Test
    void findHourlySummary_shouldReturnOnlyTargetRealmData() {
        // given
        Long itemId = 100L;
        RegionType region = RegionType.KR;
        Long realmId = 2L; // realmId 2는 itemId 100에 대한 데이터가 없음

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("7일 이전 데이터는 제외된다")
    @Test
    void findHourlySummary_shouldExcludeDataOlderThan7Days() {
        // given
        Long itemId = 100L;
        RegionType region = RegionType.KR;
        Long realmId = 1L;

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        // 6월 26일 이전 데이터는 포함되지 않아야 함
        result.forEach(history -> {
            assertThat(history.getTime()).isAfterOrEqualTo(LocalDateTime.of(2025, 6, 26, 0, 0, 0));
        });
    }

    @DisplayName("가격 변동 패턴을 확인할 수 있다")
    @Test
    void findHourlySummary_shouldShowPriceFluctuation() {
        // given
        Long itemId = 100L;
        RegionType region = RegionType.KR;
        Long realmId = 1L;

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        assertThat(result).hasSize(10);

        // 가격 변동 패턴 확인 (시간순)
        assertThat(result.get(0).getMinPrice()).isEqualTo(200L); // 6월 26일 09:00
        assertThat(result.get(1).getMinPrice()).isEqualTo(180L); // 6월 26일 14:00 (하락)
        assertThat(result.get(2).getMinPrice()).isEqualTo(220L); // 6월 27일 10:00 (상승)
        assertThat(result.get(3).getMinPrice()).isEqualTo(190L); // 6월 27일 16:00 (하락)
        assertThat(result.get(4).getMinPrice()).isEqualTo(250L); // 6월 28일 11:00 (상승)
        assertThat(result.get(5).getMinPrice()).isEqualTo(210L); // 6월 28일 18:00 (하락)
        assertThat(result.get(6).getMinPrice()).isEqualTo(230L); // 6월 29일 12:00 (상승)
        assertThat(result.get(7).getMinPrice()).isEqualTo(195L); // 6월 29일 19:00 (하락)
        assertThat(result.get(8).getMinPrice()).isEqualTo(280L); // 6월 30일 13:00 (최고가)
        assertThat(result.get(9).getMinPrice()).isEqualTo(205L); // 6월 30일 20:00 (하락)
    }

    @DisplayName("수량 변동 패턴을 확인할 수 있다")
    @Test
    void findHourlySummary_shouldShowQuantityFluctuation() {
        // given
        Long itemId = 100L;
        RegionType region = RegionType.KR;
        Long realmId = 1L;

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        assertThat(result).hasSize(10);

        // 수량 변동 패턴 확인 (시간순)
        assertThat(result.get(0).getTotalQuantity()).isEqualTo(10L); // 6월 26일 09:00
        assertThat(result.get(1).getTotalQuantity()).isEqualTo(15L); // 6월 26일 14:00 (증가)
        assertThat(result.get(2).getTotalQuantity()).isEqualTo(8L);  // 6월 27일 10:00 (감소)
        assertThat(result.get(3).getTotalQuantity()).isEqualTo(12L); // 6월 27일 16:00 (증가)
        assertThat(result.get(4).getTotalQuantity()).isEqualTo(20L); // 6월 28일 11:00 (증가)
        assertThat(result.get(5).getTotalQuantity()).isEqualTo(5L);  // 6월 28일 18:00 (감소)
        assertThat(result.get(6).getTotalQuantity()).isEqualTo(18L); // 6월 29일 12:00 (증가)
        assertThat(result.get(7).getTotalQuantity()).isEqualTo(7L);  // 6월 29일 19:00 (감소)
        assertThat(result.get(8).getTotalQuantity()).isEqualTo(25L); // 6월 30일 13:00 (최대)
        assertThat(result.get(9).getTotalQuantity()).isEqualTo(11L); // 6월 30일 20:00 (감소)
    }

    @DisplayName("최저가와 최고가를 확인할 수 있다")
    @Test
    void findHourlySummary_shouldShowMinAndMaxPrices() {
        // given
        Long itemId = 100L;
        RegionType region = RegionType.KR;
        Long realmId = 1L;

        // when
        List<AuctionHistory> result = auctionRepository.findHistory(itemId, region, realmId, now);

        // then
        assertThat(result).hasSize(10);

        // 최저가와 최고가 확인
        Long minPrice = result.stream()
                .mapToLong(AuctionHistory::getMinPrice)
                .min()
                .orElse(0L);

        Long maxPrice = result.stream()
                .mapToLong(AuctionHistory::getMinPrice)
                .max()
                .orElse(0L);

        assertThat(minPrice).isEqualTo(180L); // 6월 26일 14:00
        assertThat(maxPrice).isEqualTo(280L); // 6월 30일 13:00
    }
}
