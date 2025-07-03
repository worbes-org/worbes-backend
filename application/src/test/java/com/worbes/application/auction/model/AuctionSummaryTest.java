package com.worbes.application.auction.model;

import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.common.model.LocaleCode;
import com.worbes.application.item.model.CraftingTierType;
import com.worbes.application.item.model.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuctionSummaryTest {

    private final Item item = mock(Item.class);

    @Test
    @DisplayName("아이템 이름을 LocaleCode에 맞게 반환한다")
    void returnItemName() {
        // given
        LocaleCode locale = LocaleCode.KO_KR;
        given(item.getName(locale)).willReturn("빛나는 칼");

        AuctionSummary summary = new AuctionSummary(item,
                new SearchAuctionSummaryResult(1L, 100L, 1L));

        // when
        String name = summary.getItemName(locale);

        // then
        assertThat(name).isEqualTo("빛나는 칼");
    }

    @Test
    @DisplayName("아이템의 아이콘 URL을 반환한다")
    void returnIconUrl() {
        // given
        given(item.getIconUrl()).willReturn("https://wow.blizzard.com/icon.jpg");

        AuctionSummary summary = new AuctionSummary(item,
                new SearchAuctionSummaryResult(1L, 100L, 1L));

        // when
        String iconUrl = summary.getIconUrl();

        // then
        assertThat(iconUrl).isEqualTo("https://wow.blizzard.com/icon.jpg");
    }

    @Test
    @DisplayName("아이템의 제작 티어를 반환한다")
    void returnCraftingTierType() {
        // given
        given(item.getCraftingTier()).willReturn(CraftingTierType.THIRD);

        AuctionSummary summary = new AuctionSummary(item,
                new SearchAuctionSummaryResult(1L, 100L, 1L));

        // when
        CraftingTierType tier = summary.getCraftingTier();

        // then
        assertThat(tier).isEqualTo(CraftingTierType.THIRD);
    }
}


