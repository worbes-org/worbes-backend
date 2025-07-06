package com.worbes.application.auction.service;

import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.model.AuctionDetail;
import com.worbes.application.auction.port.in.GetAuctionDetailUseCase;
import com.worbes.application.auction.port.in.SearchAuctionSummaryCondition;
import com.worbes.application.auction.port.in.SearchAuctionSummaryUseCase;
import com.worbes.application.auction.port.out.AuctionReadRepository;
import com.worbes.application.auction.port.out.AuctionSummary;
import com.worbes.application.auction.port.out.AuctionTrend;
import com.worbes.application.item.model.Item;
import com.worbes.application.item.port.out.ItemReadRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionReadService implements SearchAuctionSummaryUseCase, GetAuctionDetailUseCase {

    private final AuctionReadRepository auctionReadRepository;
    private final ItemReadRepository itemReadRepository;

    public List<AuctionSummary> searchSummaries(SearchAuctionSummaryCondition condition) {
        return auctionReadRepository.findAllSummaryByCondition(condition);
    }

    @Override
    public AuctionDetail getDetail(Long itemId, RegionType region, Long realmId) {
        Item item = itemReadRepository.findById(itemId);
        List<Auction> activeAuctions = auctionReadRepository.findAllActiveBy(itemId, region, realmId);
        List<AuctionTrend> snapshots = auctionReadRepository.findHourlyTrendBy(itemId, region, realmId, 7);

        return new AuctionDetail(item, activeAuctions, snapshots);
    }
}
