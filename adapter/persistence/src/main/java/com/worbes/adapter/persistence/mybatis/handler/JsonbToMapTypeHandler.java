package com.worbes.adapter.persistence.mybatis.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.Collections;
import java.util.Map;

@MappedTypes(Map.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbToMapTypeHandler extends BaseTypeHandler<Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setObject(i, objectMapper.writeValueAsString(parameter), Types.OTHER);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Map to JSON", e);
        }
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public Map<String, String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private Map<String, String> parseJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSONB to Map", e);
        }
    }
}
