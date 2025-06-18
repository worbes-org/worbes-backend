package com.worbes.adapter.jpa.repository.auction;

import com.worbes.adapter.jpa.entity.AuctionEntity;
import com.worbes.adapter.jpa.mapper.AuctionEntityMapper;
import com.worbes.application.auction.model.Auction;
import com.worbes.application.auction.port.in.SearchAuctionCommand;
import com.worbes.application.auction.port.out.CreateAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionRepository;
import com.worbes.application.auction.port.out.SearchAuctionSummaryResult;
import com.worbes.application.auction.port.out.UpdateAuctionRepository;
import com.worbes.application.realm.model.RegionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class AuctionRepositoryImpl implements CreateAuctionRepository, UpdateAuctionRepository, SearchAuctionRepository {

    private final AuctionQueryRepository queryRepository;
    private final AuctionJdbcTemplate auctionJdbcTemplate;
    private final AuctionEntityMapper auctionEntityMapper;

    @Override
    public int saveAllIgnoreConflict(List<Auction> auctions) {
        List<AuctionEntity> entities = auctions.stream()
                .map(auctionEntityMapper::toEntity)
                .toList();

        return auctionJdbcTemplate.saveAllIgnoreConflict(entities);
    }

    @Override
    public Long deactivateBy(RegionType region, Long realmId, Set<Long> auctionIds) {
        return queryRepository.deactivateByRegionAndRealmAndAuctionIdsIn(region, realmId, auctionIds);
    }

    @Override
    public List<SearchAuctionSummaryResult> searchSummaries(SearchAuctionCommand command, Set<Long> itemIds) {
        return queryRepository.findAuctionSummariesBy(
                command.region(),
                command.realmId(),
                itemIds
        );
    }
}
