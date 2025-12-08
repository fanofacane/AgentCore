package com.sky.AgentCore.converter;


import com.sky.AgentCore.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
/**
 * MySQL JSON类型处理器
 * @param <T> 目标Java类型
 */
public class MySqlJsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> type;

    public MySqlJsonTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        // MySQL直接设置JSON字符串即可，驱动会自动处理
        String jsonStr = JsonUtils.toJsonString(parameter);
        // 使用setObject或setString都可以，MySQL驱动都支持
        ps.setObject(i, jsonStr, Types.JAVA_OBJECT);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    protected T parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JsonUtils.parseObject(json, type);
        } catch (Exception e) {
            throw new TypeException("Error parsing JSON string for type " + type.getName(), e);
        }
    }
}