package com.worbes.application.auction.service;

import com.worbes.application.auction.model.AuctionSnapshot;
import com.worbes.application.auction.port.in.SearchAuctionItemQuery;
import com.worbes.application.auction.port.in.SearchAuctionItemResult;
import com.worbes.application.auction.port.in.SearchAuctionItemUseCase;
import com.worbes.application.auction.port.out.FindLatestAuctionSnapshotPort;
import com.worbes.application.item.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAuctionItemService implements SearchAuctionItemUseCase {

    private final FindLatestAuctionSnapshotPort findLatestAuctionSnapshotPort;

    @Override
    public List<SearchAuctionItemResult> execute(SearchAuctionItemQuery query) {
        return findLatestAuctionSnapshotPort.findLatest(query)
                .stream()
                .map(this::toSearchAuctionItemResult)
                .toList();
    }

    private SearchAuctionItemResult toSearchAuctionItemResult(AuctionSnapshot snapshot) {
        Item item = snapshot.getItem();

        return new SearchAuctionItemResult(
                item.getId(),
                snapshot.getBonusList(),
                item.getItemLevel(snapshot.getBaseLevel(), snapshot.getBonusLevel()),
                item.getCraftingTierValue(),
                snapshot.getLowestPrice(),
                snapshot.getTotalQuantity()
        );
    }
}
