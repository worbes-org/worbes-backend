package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.application.auction.port.out.UpdateAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Integration::UpdateAuctionRepository::deactivateBy")
public class UpdateAuctionRepositoryDeactivateByTest {

    @Autowired
    private UpdateAuctionRepository updateAuctionRepository;

    @Autowired
    private AuctionJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    private static AuctionEntity createAuction(Long auctionId, RegionType region, Long realmId, boolean active) {
        return AuctionEntity.builder()
                .auctionId(auctionId)
                .itemId(1L)
                .quantity(1L)
                .unitPrice(100L)
                .buyout(1000L)
                .active(active)
                .region(region)
                .realmId(realmId)
                .build();
    }

    @Test
    @DisplayName("주어진 region, realmId, auctionIds 조건에 맞는 경매만 비활성화된다")
    void shouldDeactivateMatchingAuctionsOnly() {
        // given
        AuctionEntity target1 = createAuction(1L, RegionType.KR, 101L, true);
        AuctionEntity target2 = createAuction(2L, RegionType.KR, 101L, true);
        AuctionEntity notTarget1 = createAuction(3L, RegionType.KR, 102L, true); // 다른 realm
        AuctionEntity notTarget2 = createAuction(4L, RegionType.US, 101L, true); // 다른 region

        jpaRepository.saveAll(List.of(target1, target2, notTarget1, notTarget2));
        entityManager.flush();

        Set<Long> idsToDeactivate = Set.of(1L, 2L, 3L, 4L);

        // when
        Long updated = updateAuctionRepository.deactivateBy(RegionType.KR, 101L, idsToDeactivate);
        entityManager.clear();

        // then
        assertThat(updated).isEqualTo(2);

        AuctionEntity check1 = jpaRepository.findById(target1.getId()).orElseThrow();
        AuctionEntity check2 = jpaRepository.findById(target2.getId()).orElseThrow();
        assertThat(check1.isActive()).isFalse();
        assertThat(check2.isActive()).isFalse();

        // 다른 경매는 여전히 true
        assertThat(jpaRepository.findById(notTarget1.getId()).orElseThrow().isActive()).isTrue();
        assertThat(jpaRepository.findById(notTarget2.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("realmId가 null인 경매도 정상적으로 비활성화된다")
    void shouldDeactivateAuctionsWithNullRealmId() {
        // given
        AuctionEntity match = createAuction(10L, RegionType.KR, null, true);
        AuctionEntity notMatch = createAuction(11L, RegionType.KR, 111L, true);

        jpaRepository.saveAll(List.of(match, notMatch));
        entityManager.flush();

        // when
        Long updated = updateAuctionRepository.deactivateBy(RegionType.KR, null, Set.of(10L, 11L));
        entityManager.clear();

        // then
        assertThat(updated).isEqualTo(1);
        assertThat(jpaRepository.findById(match.getId()).orElseThrow().isActive()).isFalse();
        assertThat(jpaRepository.findById(notMatch.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("조건에 맞는 경매가 없으면 0을 반환한다")
    void shouldReturnZeroWhenNoMatch() {
        // given
        AuctionEntity entity = createAuction(20L, RegionType.KR, 999L, true);
        jpaRepository.save(entity);
        entityManager.flush();

        // 존재하지 않는 조건으로 요청
        Long updated = updateAuctionRepository.deactivateBy(RegionType.US, 111L, Set.of(20L));

        // then
        assertThat(updated).isZero();
        assertThat(jpaRepository.findById(entity.getId()).orElseThrow().isActive()).isTrue();
    }

    @Test
    @DisplayName("비활성화 시 updatedAt이 갱신된다")
    void shouldUpdateUpdatedAtWhenDeactivated() throws InterruptedException {
        // given
        AuctionEntity entity = createAuction(30L, RegionType.KR, 101L, true);
        jpaRepository.save(entity);
        entityManager.flush();

        // 최초 updatedAt 저장
        LocalDateTime beforeUpdate = jpaRepository.findById(entity.getId())
                .orElseThrow()
                .getUpdatedAt();

        // 변경 시점을 확실히 구분하기 위해 sleep
        Thread.sleep(1000);

        // when
        Long updated = updateAuctionRepository.deactivateBy(RegionType.KR, 101L, Set.of(30L));
        entityManager.clear();

        // then
        assertThat(updated).isEqualTo(1);

        AuctionEntity updatedEntity = jpaRepository.findById(entity.getId()).orElseThrow();
        assertThat(updatedEntity.isActive()).isFalse();
        assertThat(updatedEntity.getUpdatedAt()).isAfter(beforeUpdate);
    }
}
