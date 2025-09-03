package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.GetAuctionDetailQuery;
import com.worbes.application.auction.port.in.GetAuctionDetailResult;
import com.worbes.application.auction.port.out.FindAuctionPort;
import com.worbes.application.realm.model.RegionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAuctionDetailServiceTest {

    @Mock
    FindAuctionPort findAuctionPort;

    @InjectMocks
    GetAuctionDetailService service;

    @Test
    @DisplayName("경매가 없으면 예외가 발생한다")
    void execute_noAuctions_throwsException() {
        // given
        GetAuctionDetailQuery query = new GetAuctionDetailQuery(RegionType.KR, 1L, 210930L, null);
        when(findAuctionPort.findBy(query)).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> service.execute(query))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No active auctions found");
    }

    @Test
    @DisplayName("경매 상세 정보가 정상적으로 계산된다")
    void execute_returnsCorrectResult() {
        // given
        GetAuctionDetailQuery query = new GetAuctionDetailQuery(RegionType.KR, 1L, 210930L, null);
        List<Auction> auctions = List.of(
                Auction.builder().id(1L).itemId(210930L).unitPrice(1000L).quantity(2).region(RegionType.KR).build(),
                Auction.builder().id(2L).itemId(210930L).unitPrice(1200L).quantity(3).region(RegionType.KR).build(),
                Auction.builder().id(3L).itemId(210930L).unitPrice(1000L).quantity(1).region(RegionType.KR).build()
        );
        when(findAuctionPort.findBy(query)).thenReturn(auctions);

        // when
        GetAuctionDetailResult result = service.execute(query);

        // then
        assertThat(result.lowestPrice()).isEqualTo(1000L);
        assertThat(result.totalQuantity()).isEqualTo(6); // 2 + 3 + 1

        Map<Long, Integer> quantityByPrice = result.quantityByPrice();
        assertThat(quantityByPrice.keySet()).containsExactly(1000L, 1200L);
        assertThat(quantityByPrice.get(1000L)).isEqualTo(3); // 2 + 1
        assertThat(quantityByPrice.get(1200L)).isEqualTo(3);
    }
}
