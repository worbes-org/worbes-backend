package com.worbes.adapter.mybatis.common;

import com.worbes.application.realm.model.RegionType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(RegionType.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class RegionTypeHandler implements TypeHandler<RegionType> {

    @Override
    public void setParameter(PreparedStatement ps, int i, RegionType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public RegionType getResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null) {
            throw new IllegalStateException("RegionType 컬럼 '" + columnName + "' 값이 null입니다.");
        }
        return RegionType.fromValue(value);
    }

    @Override
    public RegionType getResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        if (value == null) {
            throw new IllegalStateException("RegionType 컬럼 인덱스 " + columnIndex + " 값이 null입니다.");
        }
        return RegionType.fromValue(value);
    }

    @Override
    public RegionType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        if (value == null) {
            throw new IllegalStateException("RegionType CallableStatement 인덱스 " + columnIndex + " 값이 null입니다.");
        }
        return RegionType.fromValue(value);
    }
}
