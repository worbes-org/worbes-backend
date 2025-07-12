package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.CloseAuctionUseCase;
import com.worbes.application.auction.port.out.AuctionCommandRepository;
import com.worbes.application.bonus.port.out.AuctionBonusCommandRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseAuctionUseCaseTest {

    @Mock
    private AuctionCommandRepository auctionCommandRepository;

    @Mock
    private AuctionBonusCommandRepository AuctionBonusCommandRepository;

    private CloseAuctionUseCase closeAuctionUseCase;

    @BeforeEach
    void setUp() {
        closeAuctionUseCase = new AuctionCommandService(
                auctionCommandRepository,
                AuctionBonusCommandRepository
        );
    }

    @Test
    @DisplayName("정상 케이스")
    void delegatesToRepositoryAndReturnsResult() {
        // given
        when(auctionCommandRepository.updateEndedAtBy(any(), any(), any())).thenReturn(3L);
        // when
        Long result = closeAuctionUseCase.closeAll(RegionType.KR, 1L, Set.of(1L, 2L, 3L));
        // then
        assertThat(result).isEqualTo(3L);
        verify(auctionCommandRepository, times(1)).updateEndedAtBy(eq(RegionType.KR), eq(1L), eq(Set.of(1L, 2L, 3L)));
    }

    @Nested
    @DisplayName("입력값 검증")
    class InputValidation {
        @Test
        @DisplayName("region이 null이면 예외 발생")
        void throwsExceptionWhenRegionIsNull() {
            assertThatThrownBy(() ->
                    closeAuctionUseCase.closeAll(null, 1L, Set.of(1L))
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("region은 필수");
        }

        @Test
        @DisplayName("auctionIds가 null이면 0 반환")
        void returnsZeroWhenAuctionIdsIsNull() {
            assertThatThrownBy(() ->
                    closeAuctionUseCase.closeAll(RegionType.KR, 1L, null)
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수입니다.");
            verifyNoInteractions(auctionCommandRepository);
        }

        @Test
        @DisplayName("auctionIds가 비어있으면 0 반환")
        void returnsZeroWhenAuctionIdsIsEmpty() {
            Long result = closeAuctionUseCase.closeAll(RegionType.KR, 1L, Collections.emptySet());
            assertThat(result).isZero();
            verifyNoInteractions(auctionCommandRepository);
        }
    }
} 
