package com.shopmind.productservice.handler.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * PgSQL 以 jsonb 存储数组时的类型转换器
 */
public class ArrayTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            // 将 List<String> 转为 JSON 字符串，如 ["music","travel"]
            pgObject.setValue(objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error serializing List to JSON", e);
        }
        ps.setObject(i, pgObject);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getObject(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getObject(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getObject(columnIndex));
    }

    private List<String> parseJson(Object obj) throws SQLException {
        if (obj == null) {
            return null; // 或 return Collections.emptyList();
        }

        try {
            String jsonStr;
            if (obj instanceof PGobject) {
                jsonStr = ((PGobject) obj).getValue();
            } else {
                jsonStr = obj.toString();
            }

            if (jsonStr == null || jsonStr.isEmpty() || "null".equals(jsonStr)) {
                return null;
            }

            // 反序列化为 List<String>
            return objectMapper.readValue(jsonStr, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            throw new SQLException("Failed to parse JSON to List<String>", e);
        }
    }
}