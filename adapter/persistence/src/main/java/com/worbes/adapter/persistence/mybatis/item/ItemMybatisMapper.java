package com.worbes.adapter.persistence.mybatis.item;

import com.worbes.application.item.port.in.SearchItemQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMybatisMapper {
    List<ItemMybatisDto> findBy(@Param("query") SearchItemQuery query);
}
