package com.worbes.adapter.persistence.mybatis.auction;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SaveAuctionSnapshotPortIntegrationTest {

    @Autowired
    AuctionSnapshotMybatisAdapter snapshotMybatisRepository;

    @Autowired
    AuctionMybatisAdapter auctionMybatisAdapter;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Nested
    @DisplayName("happy case")
    class HappyCase {
        @Test
        @DisplayName("여러 item_id 조합 데이터가 있을 때, 각 조합별로 snapshot이 저장된다.")
        void realmSpecific_multipleData() {
            Instant now = Instant.now();
            auctionMybatisAdapter.saveAll(List.of(
                    Auction.builder()
                            .id(1L).itemId(201L).quantity(2).buyout(1000L).region(RegionType.US).realmId(20L).itemBonus(null).build(),
                    Auction.builder()
                            .id(2L).itemId(202L).quantity(3).buyout(2000L).region(RegionType.US).realmId(20L).itemBonus(null).build()
            ));
            int result = snapshotMybatisRepository.saveAll(RegionType.US, 20L, now);
            assertThat(result).isEqualTo(2);
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM auction_snapshot WHERE region = 'US' AND realm_id = 20", Integer.class);
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("item_bonus가 없는(=null) 경매 데이터의 통계가 snapshot에 정확히 저장되는지 검증")
        void snapshotAuctionStatistics() {
            // given: item_bonus가 없는(=null) 경매 데이터 여러 건 입력
            Instant now = Instant.now();
            auctionMybatisAdapter.saveAll(List.of(
                    Auction.builder()
                            .id(21L).itemId(800L).quantity(2).buyout(1000L).region(RegionType.KR).realmId(80L).itemBonus(null).build(),
                    Auction.builder()
                            .id(22L).itemId(800L).quantity(3).buyout(900L).region(RegionType.KR).realmId(80L).itemBonus(null).build(),
                    Auction.builder()
                            .id(23L).itemId(800L).quantity(5).buyout(1200L).region(RegionType.KR).realmId(80L).itemBonus(null).build()
            ));
            int result = snapshotMybatisRepository.saveAll(RegionType.KR, 80L, now);
            assertThat(result).isEqualTo(1);
            var row = jdbcTemplate.queryForMap("SELECT * FROM auction_snapshot WHERE region = 'KR' AND realm_id = 80 AND item_id = 800");
            assertThat(row.get("lowest_price")).isEqualTo(900L);
            assertThat(row.get("total_quantity")).isEqualTo(10);
            assertThat(row.get("item_bonus")).isNull();
        }

        @Test
        @DisplayName("item_bonus가 있는 경매 데이터의 통계가 snapshot에 정확히 저장되는지 검증")
        void snapshotAuctionStatisticsWithItemBonus() {
            // given: item_bonus가 있는 경매 데이터 여러 건 입력
            Instant now = Instant.now();
            List<Long> bonus = List.of(1L, 2L, 3L);
            auctionMybatisAdapter.saveAll(List.of(
                    Auction.builder()
                            .id(31L).itemId(900L).quantity(2).buyout(1000L).region(RegionType.KR).realmId(90L).itemBonus(bonus).build(),
                    Auction.builder()
                            .id(32L).itemId(900L).quantity(3).buyout(900L).region(RegionType.KR).realmId(90L).itemBonus(bonus).build(),
                    Auction.builder()
                            .id(33L).itemId(900L).quantity(5).buyout(1200L).region(RegionType.KR).realmId(90L).itemBonus(bonus).build()
            ));
            int result = snapshotMybatisRepository.saveAll(RegionType.KR, 90L, now);
            assertThat(result).isEqualTo(1);
            var row = jdbcTemplate.queryForMap("SELECT * FROM auction_snapshot WHERE region = 'KR' AND realm_id = 90 AND item_id = 900");
            assertThat(row.get("lowest_price")).isEqualTo(900L);
            assertThat(row.get("total_quantity")).isEqualTo(10);
            assertThat(row.get("item_bonus").toString()).isEqualTo(bonus.toString());
        }

        @Test
        @DisplayName("여러 region, 여러 realmId 동시 존재: 각각 save(region, realmId, time) 호출 시 해당하는 데이터만 snapshot에 저장된다.")
        void multipleRegionRealm() {
            Instant now = Instant.now();
            auctionMybatisAdapter.saveAll(List.of(
                    Auction.builder()
                            .id(1L).itemId(500L).quantity(2).buyout(1000L).region(RegionType.KR).realmId(50L).itemBonus(null).build(),
                    Auction.builder()
                            .id(2L).itemId(501L).quantity(3).buyout(2000L).region(RegionType.KR).realmId(50L).itemBonus(null).build(),
                    Auction.builder()
                            .id(3L).itemId(600L).quantity(4).buyout(3000L).region(RegionType.US).realmId(60L).itemBonus(null).build(),
                    Auction.builder()
                            .id(4L).itemId(601L).quantity(5).buyout(4000L).region(RegionType.US).realmId(60L).itemBonus(null).build()
            ));
            int resultKR = snapshotMybatisRepository.saveAll(RegionType.KR, 50L, now);
            int resultUS = snapshotMybatisRepository.saveAll(RegionType.US, 60L, now);
            assertThat(resultKR).isEqualTo(2);
            assertThat(resultUS).isEqualTo(2);
            Integer countKR = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM auction_snapshot WHERE region = 'KR' AND realm_id = 50", Integer.class);
            Integer countUS = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM auction_snapshot WHERE region = 'US' AND realm_id = 60", Integer.class);
            assertThat(countKR).isEqualTo(2);
            assertThat(countUS).isEqualTo(2);
        }

        @Test
        @DisplayName("같은 item_id이지만 item_bonus가 다르면 각각 별도의 snapshot row로 저장된다.")
        void sameItemIdDifferentBonus() {
            Instant now = Instant.now();
            List<Long> bonus1 = List.of(1L, 2L);
            List<Long> bonus2 = List.of(3L, 4L);
            auctionMybatisAdapter.saveAll(List.of(
                    Auction.builder()
                            .id(100L).itemId(1000L).quantity(2).buyout(1000L).region(RegionType.KR).realmId(10L).itemBonus(bonus1).build(),
                    Auction.builder()
                            .id(101L).itemId(1000L).quantity(3).buyout(2000L).region(RegionType.KR).realmId(10L).itemBonus(bonus2).build()
            ));
            int result = snapshotMybatisRepository.saveAll(RegionType.KR, 10L, now);
            assertThat(result).isEqualTo(2);
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM auction_snapshot WHERE region = 'KR' AND realm_id = 10 AND item_id = 1000", Integer.class);
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("edge case")
    class EdgeCase {
        @Test
        @DisplayName("auction 테이블에 해당 region/realm 데이터가 없으면 0을 반환한다.")
        void save_noData() {
            int result = snapshotMybatisRepository.saveAll(RegionType.KR, 999L, Instant.now());
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("region wide insert에서 realm_id가 null인 데이터만 집계된다.")
        void regionWide_onlyNullRealmId() {
            Instant now = Instant.now();
            // realm_id가 null인 데이터만 region wide에 포함되어야 함
            auctionMybatisAdapter.saveAll(List.of(
                    Auction.builder()
                            .id(1L).itemId(100L).quantity(2).unitPrice(1000L).region(RegionType.KR).build(),
                    Auction.builder()
                            .id(2L).itemId(101L).quantity(3).unitPrice(2000L).region(RegionType.KR).realmId(10L).itemBonus(null).build()
            ));
            int result = snapshotMybatisRepository.saveAll(RegionType.KR, null, now);
            assertThat(result).isEqualTo(1); // realm_id가 null인 1건만 포함
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM auction_snapshot WHERE region = 'KR' AND realm_id IS NULL", Integer.class);
            assertThat(count).isEqualTo(1);
        }
    }
}
