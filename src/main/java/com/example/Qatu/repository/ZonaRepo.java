package com.example.Qatu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Qatu.dto.ZonaOcupacionProjectionDTO;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.TipoZona;

public interface ZonaRepo extends GenericRepo<Zona, Integer> {
  // Todas las zonas activas para mostrar
  List<Zona> findByActivaTrue();

  // Zonas activas por tipo
  List<Zona> findByTipoZonaAndActivaTrue(TipoZona tipoZona);

  // Verificar si un punto está dentro de una zona restringida
  @Query(value = """
      SELECT * FROM zonas z
      WHERE z.activa = true
        AND z.tipo_zona = 'RESTRINGIDA'
        AND ST_Within(
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography::geometry,
              z.geometria::geometry
            )
      """, nativeQuery = true)
  List<Zona> findZonasRestringidasQueContienenPunto(
      @Param("lat") double lat,
      @Param("lng") double lng);

  // Zonas de reasignación disponibles (menos del 40% de capacidad)

  @Query(value = """
      SELECT z.id,
             z.nombre,
             z.tipo_zona AS tipoZona,
             z.capacidad_maxima AS capacidadMaxima,
             (
                 SELECT COUNT(*) FROM ubicaciones u
                 WHERE u.activo = true
                 AND ST_Within(
                     u.coordenada::geometry,
                     z.geometria::geometry
                 )
             ) AS vendedoresActuales
      FROM zonas z
      WHERE z.activa = true
        AND ST_Within(
              ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography::geometry,
              z.geometria::geometry
            )
      """, nativeQuery = true)
  List<ZonaOcupacionProjectionDTO> findZonasConOcupacionEnPunto(
      @Param("lat") double lat,
      @Param("lng") double lng);

  @Query(value = """
      SELECT z.* FROM zonas z
      WHERE z.activa = true
        AND z.tipo_zona = 'REASIGNACION'
        AND (
          SELECT COUNT(*) FROM ubicaciones u
          WHERE u.activo = true
            AND ST_Within(
                  u.coordenada::geometry,
                  z.geometria::geometry
                )
        ) < (z.capacidad_maxima * 0.4)
      ORDER BY ST_Distance(
          z.geometria::geography,
          ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
      ) ASC
      LIMIT 3
      """, nativeQuery = true)
  List<Zona> findZonasReasignacionDisponibles(
      @Param("lat") double lat,
      @Param("lng") double lng);
}
