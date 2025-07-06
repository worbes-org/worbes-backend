package com.worbes.application.auction.service;

import com.worbes.application.auction.port.in.EndAuctionUseCase;
import com.worbes.application.auction.port.out.UpdateAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
@DisplayName("UpdateAuctionService::closeAuctions 단위 테스트")
class EndAuctionUseCaseTest {

    @Mock
    private UpdateAuctionRepository updateAuctionRepository;

    @InjectMocks
    private EndAuctionUseCase endAuctionUseCase;

    @Test
    @DisplayName("정상 동작: repository에 위임 및 결과 반환")
    void delegatesToRepositoryAndReturnsResult() {
        // given
        when(updateAuctionRepository.updateEndedAt(any(), any(), any())).thenReturn(3L);
        // when
        Long result = endAuctionUseCase.end(RegionType.KR, 1L, Set.of(1L, 2L, 3L));
        // then
        assertThat(result).isEqualTo(3L);
        verify(updateAuctionRepository, times(1)).updateEndedAt(eq(RegionType.KR), eq(1L), eq(Set.of(1L, 2L, 3L)));
    }

    @Nested
    @DisplayName("입력값 검증")
    class InputValidation {
        @Test
        @DisplayName("region이 null이면 예외 발생")
        void throwsExceptionWhenRegionIsNull() {
            assertThatThrownBy(() ->
                    endAuctionUseCase.end(null, 1L, Set.of(1L))
            ).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("region은 필수");
        }

        @Test
        @DisplayName("auctionIds가 null이면 0 반환")
        void returnsZeroWhenAuctionIdsIsNull() {
            Long result = endAuctionUseCase.end(RegionType.KR, 1L, null);
            assertThat(result).isZero();
            verifyNoInteractions(updateAuctionRepository);
        }

        @Test
        @DisplayName("auctionIds가 비어있으면 0 반환")
        void returnsZeroWhenAuctionIdsIsEmpty() {
            Long result = endAuctionUseCase.end(RegionType.KR, 1L, Collections.emptySet());
            assertThat(result).isZero();
            verifyNoInteractions(updateAuctionRepository);
        }
    }
} 
