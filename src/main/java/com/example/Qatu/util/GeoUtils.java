package com.example.Qatu.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.*;

public class GeoUtils {

    private static final GeometryFactory FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    // recibe (x=lng, y=lat)
    public static Point crearPunto(double lat, double lng) {
        return FACTORY.createPoint(new Coordinate(lng, lat));
    }

    public static double getLat(Point point) {
        return point.getY(); // Y = latitud
    }

    public static double getLng(Point point) {
        return point.getX(); // X = longitud
    }

    // En tu clase GeoUtils existente, agrega este método:
    public static Polygon crearPoligono(List<List<Double>> coordenadas) {
        Coordinate[] coords = coordenadas.stream()
                .map(c -> new Coordinate(c.get(0), c.get(1))) // [lng, lat]
                .toArray(Coordinate[]::new);

        return FACTORY.createPolygon(coords);
    }

    // Y para extraer coordenadas del polígono al response:
    public static List<List<Double>> extraerCoordenadas(Polygon polygon) {
        return Arrays.stream(polygon.getCoordinates())
                .map(c -> List.of(c.x, c.y)) // [lng, lat]
                .collect(Collectors.toList());
    }

    public static double[] calcularCentroide(Polygon polygon) {
        Point centroide = polygon.getCentroid();
        return new double[] { centroide.getY(), centroide.getX() }; // lat, lng
    }
}