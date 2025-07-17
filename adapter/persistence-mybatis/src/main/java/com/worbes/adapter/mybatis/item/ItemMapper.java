package com.worbes.adapter.mybatis.item;

import com.worbes.application.item.port.in.SearchItemQuery;
import com.worbes.application.item.port.out.FindItemResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<FindItemResult> findItemsBySearchQuery(@Param("query") SearchItemQuery query);
}
