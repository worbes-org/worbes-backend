package com.worbes.adapter.persistence.jpa.auction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Sql(scripts = "/sql/auction/insert_old_and_recent_auction_snapshots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class DeleteAuctionSnapshotPortTest {

    @Autowired
    AuctionSnapshotJpaAdapter adapter;

    @Autowired
    AuctionSnapshotJpaRepository repository;

    @Test
    @DisplayName("30일 이상 지난 데이터만 정확히 삭제된다")
    void deleteOlderThan_shouldDeleteRowsOlderThanGivenInstant() {
        // given
        long beforeCount = repository.count();
        assertThat(beforeCount).isEqualTo(100L);

        // when
        long deleted = adapter.deleteOlderThanOneMonth();

        // then
        long afterCount = repository.count();

        assertThat(deleted).isEqualTo(50);
        assertThat(afterCount).isEqualTo(50);
    }
}
