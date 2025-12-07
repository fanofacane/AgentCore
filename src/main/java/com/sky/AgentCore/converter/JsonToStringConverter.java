package com.sky.AgentCore.converter;


import com.sky.AgentCore.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** JSON类型转换器 用于处理数据库JSONB类型和Java对象之间的转换
 * @param <T> 要转换的对象类型 */
@MappedJdbcTypes(JdbcType.OTHER)
public abstract class JsonToStringConverter<T> extends BaseTypeHandler<T> {

    private final Class<T> type;

    protected JsonToStringConverter(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    /**
     * 设置非空参数（Java对象 → MySQL JSON 字段）
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 1. 将 Java 对象转为 JSON 字符串
            String jsonStr = JsonUtils.toJsonString(parameter);
            // 2. MySQL 直接绑定 JSON 字符串（驱动会自动处理为 JSON 类型）
            ps.setString(i, jsonStr);
        } catch (Exception e) {
            throw new SQLException("转换 Java 对象为 JSON 字符串失败", e);
        }
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
        if (json == null) {
            return null;
        }
        return JsonUtils.parseObject(json, type);
    }
}
