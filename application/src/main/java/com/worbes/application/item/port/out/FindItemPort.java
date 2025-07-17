package com.worbes.application.item.port.out;

import com.worbes.application.item.port.in.SearchItemQuery;

import java.util.List;

public interface FindItemPort {
    List<FindItemResult> findItemsBySearchQuery(SearchItemQuery query);
}
