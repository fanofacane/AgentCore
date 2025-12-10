package com.sky.AgentCore.converter;


import com.alibaba.fastjson2.JSON;
import com.sky.AgentCore.dto.user.UserSettingsConfig;
import com.sky.AgentCore.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 用户设置配置转换器 处理JSON存储的用户设置配置信息 */
@MappedTypes(UserSettingsConfig.class)
@Component
@MappedJdbcTypes({JdbcType.OTHER})
public class UserSettingsConfigConverter extends BaseTypeHandler<UserSettingsConfig> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserSettingsConfig parameter, JdbcType jdbcType)
            throws SQLException {
        String jsonStr = JSON.toJSONString(parameter);
        ps.setString(i, jsonStr);
    }

    @Override
    public UserSettingsConfig getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public UserSettingsConfig getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public UserSettingsConfig getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private UserSettingsConfig parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty() || "null".equals(json)) {
            return new UserSettingsConfig();
        }
        try {
            UserSettingsConfig config = JsonUtils.parseObject(json, UserSettingsConfig.class);
            return config != null ? config : new UserSettingsConfig();
        } catch (Exception e) {
            // 如果解析失败，返回默认实例
            return new UserSettingsConfig();
        }
    }

}
