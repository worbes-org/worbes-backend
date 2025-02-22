package com.worbes.auctionhousetracker.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worbes.auctionhousetracker.dto.response.AuctionResponse;
import com.worbes.auctionhousetracker.entity.Auction;
import com.worbes.auctionhousetracker.entity.enums.Region;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuctionRepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Rollback(false)
    @DisplayName("auction-response.json 파일로 경매 데이터 저장 테스트")
    void saveAll_ShouldPersist100kAuctionsEfficiently() throws IOException {
        // Given
        ObjectMapper objectMapper = new ObjectMapper();
        AuctionResponse response = objectMapper.readValue(
                getClass().getResourceAsStream("/json/auction-response.json"),
                AuctionResponse.class
        );

        List<Auction> auctions = response.getAuctions().stream()
                .map(dto -> new Auction(dto, Region.KR))
                .toList();
        int batchSize = 1000;

        // When
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < auctions.size(); i += batchSize) {
            List<Auction> batch = auctions.subList(i, Math.min(i + batchSize, auctions.size()));
            auctionRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
        }

        // Then
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("🔥 실제 경매 데이터 {}건 저장 완료! 실행 시간: {} ms", auctions.size(), duration);

        assertEquals(auctions.size(), auctionRepository.count());
    }
}
