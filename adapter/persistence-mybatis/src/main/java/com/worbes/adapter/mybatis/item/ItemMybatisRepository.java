package com.worbes.adapter.mybatis.item;

import com.worbes.application.item.port.in.SearchItemQuery;
import com.worbes.application.item.port.out.FindItemPort;
import com.worbes.application.item.port.out.FindItemResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemMybatisRepository implements FindItemPort {

    private final ItemMapper mapper;

    @Override
    public List<FindItemResult> findItemsBySearchQuery(SearchItemQuery query) {
        return mapper.findItemsBySearchQuery(query).stream()
                .toList();
    }
}
