package com.worbes.application.auction.model;

import com.worbes.application.auction.port.out.AuctionTrend;
import com.worbes.application.item.model.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class AuctionDetail {
    private final Item item;
    private final List<Auction> auctions;
    private final List<AuctionTrend> trends;

    public Map<Long, Long> getAvailable() {
        return auctions.stream()
                .collect(Collectors.groupingBy(
                        Auction::getPrice,
                        TreeMap::new,
                        Collectors.summingLong(Auction::getQuantity)
                ));
    }

    public Double getLowestPriceChangeRateSince(LocalDateTime baseTime) {
        if (trends == null || trends.isEmpty()) return null;

        // trends를 시간순 정렬
        List<AuctionTrend> sorted = trends.stream()
                .sorted(Comparator.comparing(AuctionTrend::time))
                .toList();

        // baseTime과 가장 가까운 데이터 찾기 (baseTime보다 같거나 이전 중 가장 최근)
        AuctionTrend baseTrend = null;
        for (AuctionTrend t : sorted) {
            if (!t.time().isAfter(baseTime)) {
                baseTrend = t;
            } else {
                break;
            }
        }

        // now와 가장 가까운 데이터 (가장 최근 데이터)
        AuctionTrend nowTrend = sorted.get(sorted.size() - 1);

        if (baseTrend == null || nowTrend.lowestPrice() == null || baseTrend.lowestPrice() == null) return null;
        if (baseTrend.lowestPrice() == 0) return null; // 0으로 나누기 방지

        return ((double) (nowTrend.lowestPrice() - baseTrend.lowestPrice()) / baseTrend.lowestPrice()) * 100;
    }
}
