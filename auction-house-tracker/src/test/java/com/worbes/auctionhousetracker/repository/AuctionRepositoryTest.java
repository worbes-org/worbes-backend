package com.worbes.auctionhousetracker.repository;

import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.worbes.auctionhousetracker.TestUtils.createRandomAuctionDtos;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuctionRepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Test
    @DisplayName("경매 데이터 1만 건 저장 성능 테스트")
    void saveAll_ShouldPersist100kAuctionsEfficiently() {
        // Given
        int dataSize = 10000;  // 총 1000개 저장
        int batchSize = 1000;  // 🔥 100개씩 배치 실행
        List<Auction> auctions = createRandomAuctionDtos(dataSize).stream()
                .map(dto -> new Auction(dto, Region.KR))
                .toList();

        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < dataSize; i += batchSize) {  // 💡 100개씩 저장
            List<Auction> batch = auctions.subList(i, Math.min(i + batchSize, dataSize));
            auctionRepository.saveAll(batch);
            entityManager.flush();  // 💡 배치 INSERT 강제 실행
            entityManager.clear();  // 💡 영속성 컨텍스트 초기화 (메모리 절약)
        }

        // Then
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("🔥 1000건 저장 완료! 실행 시간: {} ms", duration);

        assertEquals(dataSize, auctionRepository.count()); // 저장된 개수 검증
    }
}
