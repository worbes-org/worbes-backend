package com.worbes.application.auction.model;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuctionSummaryTest {

    private final Item item = mock(Item.class);

    @Test
    @DisplayName("unitPrice만 존재하면 unitPrice를 가격으로 사용한다")
    void unitPrice만_있을_때() {
        // given
        Long unitPrice = 100L;
        Long available = 3L;
        SearchAuctionSummaryResult summaryResult = new SearchAuctionSummaryResult(1L, unitPrice, null, available);

        // when
        AuctionSummary summary = new AuctionSummary(item, summaryResult);

        // then
        assertThat(summary.getPrice()).isEqualTo(new Price(100L));
        assertThat(summary.getAvailable()).isEqualTo(available);
    }

    @Test
    @DisplayName("buyout만 존재하면 buyout을 가격으로 사용한다")
    void buyout만_있을_때() {
        // given
        Long buyout = 250L;
        Long available = 5L;
        SearchAuctionSummaryResult summaryResult = new SearchAuctionSummaryResult(1L, null, buyout, available);

        // when
        AuctionSummary summary = new AuctionSummary(item, summaryResult);

        // then
        assertThat(summary.getPrice()).isEqualTo(new Price(250L));
        assertThat(summary.getAvailable()).isEqualTo(available);
    }

    @Test
    @DisplayName("unitPrice와 buyout이 모두 null이거나 0이면 예외가 발생한다")
    void 가격이_모두_null_또는_0이면_예외() {
        // given
        Long unitPrice = null;
        Long buyout = null;
        SearchAuctionSummaryResult summaryResult = new SearchAuctionSummaryResult(1L, unitPrice, buyout, 1L);

        // when & then
        assertThatThrownBy(() -> new AuctionSummary(item, summaryResult))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("buyout and unitPrice are both null or zero");
    }

    @Test
    @DisplayName("아이템 이름을 LocaleCode에 맞게 반환한다")
    void 이름_반환() {
        // given
        LocaleCode locale = LocaleCode.KO_KR;
        given(item.getName(locale)).willReturn("빛나는 칼");

        AuctionSummary summary = new AuctionSummary(item,
                new SearchAuctionSummaryResult(1L, 100L, null, 1L));

        // when
        String name = summary.getItemName(locale);

        // then
        assertThat(name).isEqualTo("빛나는 칼");
    }

    @Test
    @DisplayName("아이템의 아이콘 URL을 반환한다")
    void 아이콘_URL_반환() {
        // given
        given(item.getIconUrl()).willReturn("https://wow.blizzard.com/icon.jpg");

        AuctionSummary summary = new AuctionSummary(item,
                new SearchAuctionSummaryResult(1L, 100L, null, 1L));

        // when
        String iconUrl = summary.getIconUrl();

        // then
        assertThat(iconUrl).isEqualTo("https://wow.blizzard.com/icon.jpg");
    }

    @Test
    @DisplayName("아이템의 제작 티어를 반환한다")
    void 제작_티어_반환() {
        // given
        given(item.getCraftingTier()).willReturn(CraftingTierType.THIRD);

        AuctionSummary summary = new AuctionSummary(item,
                new SearchAuctionSummaryResult(1L, 100L, null, 1L));

        // when
        CraftingTierType tier = summary.getCraftingTier();

        // then
        assertThat(tier).isEqualTo(CraftingTierType.THIRD);
    }
}


