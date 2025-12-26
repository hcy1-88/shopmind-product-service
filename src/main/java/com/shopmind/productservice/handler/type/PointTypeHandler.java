package com.shopmind.productservice.handler.type;

import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.Point;

/**
 * Point的类型转换器
 */
@MappedTypes({Point.class})
public class PointTypeHandler extends AbstractGeographyTypeHandler<Point> {
}