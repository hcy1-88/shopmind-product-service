package com.shopmind.productservice.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Geography 字段和 MyBatis 类型转换器（适用于 PostGIS geography 类型）
 *
 * @param <T> 具体的几何类型（如 Point、Polygon 等）
 */
public abstract class AbstractGeographyTypeHandler<T extends Geometry> extends BaseTypeHandler<T> {

    /**
     * WKTReader 非线程安全，使用 ThreadLocal
     */
    private static final ThreadLocal<WKTReader> READER_POOL = ThreadLocal.withInitial(() -> {
        GeometryFactory factory = new GeometryFactory();
        return new WKTReader(factory);
    });

    /**
     * WKTWriter 非线程安全，使用 ThreadLocal
     */
    private static final ThreadLocal<WKTWriter> WRITER_POOL = ThreadLocal.withInitial(WKTWriter::new);

    /**
     * 数据库中 geography 列的 SRID，通常为 4326（WGS84）
     */
    private static final int SRID_IN_DB = 4326;

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, java.sql.Types.OTHER);
            return;
        }

        // 设置 SRID（JTS 默认无 SRID，需显式设置）
        parameter.setSRID(SRID_IN_DB);

        // 生成 WKT：如 "SRID=4326;POINT(116.4 39.9)"
        String wkt = "SRID=" + SRID_IN_DB + ";" + WRITER_POOL.get().write(parameter);

        // 使用 PGobject 包装 geography 类型
        PGobject pgObject = new PGobject();
        pgObject.setType("geography"); // ← 关键：指定为 geography, 与数据库对应
        pgObject.setValue(wkt);

        ps.setObject(i, pgObject);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseGeography(rs.getObject(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseGeography(rs.getObject(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseGeography(cs.getObject(columnIndex));
    }

    @SuppressWarnings("unchecked")
    private T parseGeography(Object obj) throws SQLException {
        if (obj == null) {
            return null;
        }

        try {
            // PostgreSQL 返回的是 PGobject
            if (obj instanceof PGobject) {
                String wktWithSrid  = ((PGobject) obj).getValue();
                if (wktWithSrid  == null || wktWithSrid .isEmpty()) {
                    return null;
                }
                // 数据库返回格式 如 SRID=4326;POINT(111.51347631188159 68.48589996685493)，这里需去掉 【SRID=4326;】 前缀，否则 WKTReader 不能解析
                String pureWkt;
                if (wktWithSrid.startsWith("SRID=")) {
                    int semicolonIndex = wktWithSrid.indexOf(';');
                    if (semicolonIndex != -1) {
                        pureWkt = wktWithSrid.substring(semicolonIndex + 1);
                    } else {
                        pureWkt = wktWithSrid; // fallback，虽然不应该发生
                    }
                } else {
                    pureWkt = wktWithSrid;
                }

                // 使用 WKTReader 解析纯 WKT
                Geometry geometry = READER_POOL.get().read(pureWkt);
                return (T) geometry;
            } else {
                throw new SQLException("Expected PGobject for geography column, but got: " + obj.getClass());
            }
        } catch (Exception e) {
            throw new SQLException("Failed to parse geography from WKT: " + obj, e);
        }
    }
}