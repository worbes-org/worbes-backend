package com.worbes.adapter.persistence.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.List;

@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbListTypeHandler extends BaseTypeHandler<List<Long>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, objectMapper.writeValueAsString(parameter), Types.OTHER);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize itemBonus to JSON", e);
        }
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toList(rs.getString(columnName));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toList(rs.getString(columnIndex));
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toList(cs.getString(columnIndex));
    }

    private List<Long> toList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize itemBonus JSON", e);
        }
    }
}
