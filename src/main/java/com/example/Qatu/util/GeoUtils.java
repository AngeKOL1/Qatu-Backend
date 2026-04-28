package com.example.Qatu.util;

import org.locationtech.jts.geom.*;

public class GeoUtils {

    private static final GeometryFactory FACTORY =
        new GeometryFactory(new PrecisionModel(), 4326);

    //recibe (x=lng, y=lat) 
    public static Point crearPunto(double lat, double lng) {
        return FACTORY.createPoint(new Coordinate(lng, lat));
    }

    public static double getLat(Point point) {
        return point.getY(); // Y = latitud
    }

    public static double getLng(Point point) {
        return point.getX(); // X = longitud
    }
}