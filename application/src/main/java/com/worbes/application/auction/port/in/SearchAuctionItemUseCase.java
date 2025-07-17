package com.worbes.application.auction.port.in;

import java.util.List;

public interface SearchAuctionItemUseCase {
    List<SearchAuctionItemResult> execute(SearchAuctionItemQuery query);
}
