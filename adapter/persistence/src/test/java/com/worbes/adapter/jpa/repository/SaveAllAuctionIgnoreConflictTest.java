package com.worbes.adapter.jpa.repository;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.out.CreateAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Integration::CreateAuctionRepository::saveAllIgnoreConflict")
public class SaveAllAuctionIgnoreConflictTest {

    private final RegionType region = RegionType.KR;
    private final Long realmId = 101L;

    @Autowired
    private CreateAuctionRepository createAuctionRepository;

    @Autowired
    private AuctionJpaRepository auctionJpaRepository;

    private Auction createAuction(Long auctionId, RegionType region, Long realmId) {
        return Auction.builder()
                .id(auctionId)
                .itemId(1L)
                .quantity(5L)
                .unitPrice(500L)
                .buyout(1000L)
                .active(true)
                .region(region)
                .realmId(realmId)
                .build();
    }

    @Test
    @DisplayName("중복 auctionId가 있을 경우 무시하고 나머지를 저장한다")
    void givenDuplicateAuctionIds_whenSaveAllIgnoreConflict_thenDuplicatesIgnored() {
        // given
        Auction auction1 = createAuction(1001L, region, realmId);
        Auction auction2 = createAuction(1002L, region, realmId);
        Auction auction3 = createAuction(1001L, region, realmId); // 중복 ID

        // when
        int saved = createAuctionRepository.saveAllIgnoreConflict(List.of(auction1, auction2));
        int conflict = createAuctionRepository.saveAllIgnoreConflict(List.of(auction3));// 중복 저장 시도

        // then
        List<AuctionEntity> savedEntities = auctionJpaRepository.findAll();

        // 중복은 저장되지 않음
        assertThat(savedEntities).hasSize(2);
        assertThat(saved).isEqualTo(savedEntities.size());
        assertThat(conflict).isEqualTo(0);

        Set<Long> savedAuctionIds = savedEntities.stream()
                .map(AuctionEntity::getAuctionId)
                .collect(Collectors.toSet());

        assertThat(savedAuctionIds)
                .as("중복 auctionId는 무시되어야 한다")
                .containsExactlyInAnyOrder(1001L, 1002L);
    }

    @Test
    @DisplayName("빈 리스트 저장 시 아무 것도 저장하지 않는다")
    void givenEmptyList_whenSaveAllIgnoreConflict_thenNothingSaved() {
        // when
        int saved = createAuctionRepository.saveAllIgnoreConflict(Collections.emptyList());

        // then
        List<AuctionEntity> savedEntities = auctionJpaRepository.findAll();
        assertThat(savedEntities).isEmpty();
        assertThat(saved).isZero();
    }

    @Test
    @DisplayName("중복 없이 여러 건 저장 시 모두 저장된다")
    void givenUniqueAuctions_whenSaveAllIgnoreConflict_thenAllSaved() {
        // given
        List<Auction> auctions = List.of(
                createAuction(2001L, region, realmId),
                createAuction(2002L, region, realmId),
                createAuction(2003L, region, realmId)
        );

        // when
        int saved = createAuctionRepository.saveAllIgnoreConflict(auctions);

        // then
        List<AuctionEntity> savedEntities = auctionJpaRepository.findAll();
        assertThat(savedEntities).hasSize(3);
        assertThat(saved).isEqualTo(3);
    }

    @Test
    @DisplayName("중복된 auctionId 여러 개가 섞여 있을 때 중복만 무시하고 저장한다")
    void givenMixedDuplicates_whenSaveAllIgnoreConflict_thenSaveOnlyNonDuplicates() {
        // given
        Auction auction1 = createAuction(3001L, region, realmId);
        Auction auction2 = createAuction(3002L, region, realmId);
        createAuctionRepository.saveAllIgnoreConflict(List.of(auction1));

        Auction duplicate1 = createAuction(3001L, region, realmId); // 중복
        Auction newAuction = createAuction(3003L, region, realmId);

        // when
        int saved = createAuctionRepository.saveAllIgnoreConflict(List.of(duplicate1, auction2, newAuction));

        // then
        List<AuctionEntity> savedEntities = auctionJpaRepository.findAll();
        assertThat(savedEntities).hasSize(3);
        assertThat(saved).isEqualTo(2);
    }

    @Test
    @DisplayName("auctionId가 null이면 예외가 발생한다")
    void givenNullAuctionId_whenSaveAllIgnoreConflict_thenExceptionThrown() {
        // given
        Auction badAuction = Auction.builder()
                .id(null)
                .itemId(1L)
                .quantity(5L)
                .unitPrice(500L)
                .buyout(1000L)
                .active(true)
                .region(region)
                .realmId(realmId)
                .build();

        // when, then
        assertThatThrownBy(() -> createAuctionRepository.saveAllIgnoreConflict(List.of(badAuction)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("여러 번 중복 저장 시 중복은 무시되고 상태 유지")
    void givenRepeatedDuplicates_whenSaveAllIgnoreConflict_thenNoDuplicateInsertion() {
        // given
        Auction auction = createAuction(4001L, region, realmId);

        // when
        int firstSave = createAuctionRepository.saveAllIgnoreConflict(List.of(auction));
        int secondSave = createAuctionRepository.saveAllIgnoreConflict(List.of(auction));
        int thirdSave = createAuctionRepository.saveAllIgnoreConflict(List.of(auction));

        // then
        List<AuctionEntity> savedEntities = auctionJpaRepository.findAll();
        assertThat(savedEntities).hasSize(1);
        assertThat(firstSave).isEqualTo(1);
        assertThat(secondSave).isEqualTo(0);
        assertThat(thirdSave).isEqualTo(0);
    }

    @Test
    @DisplayName("nullable 필드는 null 값으로 저장 가능하다")
    void givenNullableFieldsNull_whenSaveAllIgnoreConflict_thenSaveSuccess() {
        // given
        Auction auctionWithNulls = Auction.builder()
                .id(5001L)
                .itemId(1L)
                .quantity(5L)
                .unitPrice(null)
                .buyout(null)
                .active(true)
                .region(region)
                .realmId(realmId)
                .build();

        // when
        int saved = createAuctionRepository.saveAllIgnoreConflict(List.of(auctionWithNulls));

        // then
        List<AuctionEntity> savedEntities = auctionJpaRepository.findAll();
        assertThat(savedEntities).hasSize(1);
        AuctionEntity entity = savedEntities.get(0);
        assertThat(entity.getUnitPrice()).isNull();
        assertThat(entity.getBuyout()).isNull();
        assertThat(saved).isEqualTo(1);
    }
}
