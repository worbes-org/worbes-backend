package com.worbes.adapter.jpa.auction;

import com.worbes.application.auction.port.out.AuctionCommandRepository;
import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "auction-cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class AuctionCommandRepositoryUpdateEndedAtByTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 101L;

    @Autowired
    private AuctionCommandRepository auctionCommandRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private AuctionEntity createAuction(Long auctionId, RegionType region, Long realmId) {
        return AuctionEntity.builder()
                .id(auctionId)
                .itemId(1L)
                .quantity(1)
                .price(100L)
                .region(region)
                .realmId(realmId)
                .build();
    }

    @Nested
    @DisplayName("정상 케이스")
    class HappyCases {
        @Test
        @DisplayName("조건에 맞는 경매만 종료된다")
        void onlyMatchingAuctionsAreEnded() {
            // given
            AuctionEntity target1 = createAuction(1L, region, realmId);
            AuctionEntity target2 = createAuction(2L, region, realmId);
            AuctionEntity notTarget1 = createAuction(3L, region, 102L); // 다른 realm
            AuctionEntity notTarget2 = createAuction(4L, RegionType.US, realmId); // 다른 region

            auctionJpaRepository.saveAll(List.of(target1, target2, notTarget1, notTarget2));
            entityManager.flush();

            Set<Long> idsToEnd = Set.of(1L, 2L, 3L, 4L);

            // when
            Long updated = auctionCommandRepository.updateEndedAtBy(region, realmId, idsToEnd);
            entityManager.clear();

            // then
            assertThat(updated).isEqualTo(2);
            AuctionEntity check1 = auctionJpaRepository.findById(target1.getId()).orElseThrow();
            AuctionEntity check2 = auctionJpaRepository.findById(target2.getId()).orElseThrow();
            assertThat(check1.getEndedAt()).isNotNull();
            assertThat(check2.getEndedAt()).isNotNull();
            // 조건 불일치 경매는 그대로
            assertThat(auctionJpaRepository.findById(notTarget1.getId()).orElseThrow().getEndedAt()).isNull();
            assertThat(auctionJpaRepository.findById(notTarget2.getId()).orElseThrow().getEndedAt()).isNull();
        }

        @Test
        @DisplayName("realmId가 null인 경매도 정상적으로 종료된다")
        void endsAuctionsWithNullRealmId() {
            // given
            AuctionEntity match = createAuction(10L, region, null);
            AuctionEntity notMatch = createAuction(11L, region, 111L);
            auctionJpaRepository.saveAll(List.of(match, notMatch));
            entityManager.flush();

            // when
            Long updated = auctionCommandRepository.updateEndedAtBy(region, null, Set.of(10L, 11L));
            entityManager.clear();

            // then
            assertThat(updated).isEqualTo(1);
            assertThat(auctionJpaRepository.findById(match.getId()).orElseThrow().getEndedAt()).isNotNull();
            assertThat(auctionJpaRepository.findById(notMatch.getId()).orElseThrow().getEndedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("경계/실패 케이스")
    class EdgeAndFailCases {
        @Test
        @DisplayName("조건에 맞는 경매가 없으면 0을 반환한다")
        void returnsZeroWhenNoMatchingAuctions() {
            // given
            AuctionEntity entity = createAuction(20L, region, 999L);
            auctionJpaRepository.save(entity);
            entityManager.flush();

            // when
            Long updated = auctionCommandRepository.updateEndedAtBy(RegionType.US, 111L, Set.of(20L));

            // then
            assertThat(updated).isZero();
            assertThat(auctionJpaRepository.findById(entity.getId()).orElseThrow().getEndedAt()).isNull();
        }
    }
}
