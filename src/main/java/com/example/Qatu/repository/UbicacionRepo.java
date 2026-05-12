package com.example.Qatu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Qatu.models.Ubicacion;

public interface UbicacionRepo extends GenericRepo<Ubicacion, Integer> {
    // Desactivar ubicación anterior del vendedor
    @Modifying
    @Query("UPDATE Ubicacion u SET u.activo = false WHERE u.vendedor.id = :vendedorId")
    void desactivarPorVendedor(@Param("vendedorId") Integer vendedorId);

    @Query(value = """
        SELECT COUNT(*) FROM ubicaciones u
        JOIN vendedores v ON v.id = u.vendedor_id
        WHERE u.activo = true
        AND v.estado = 'ACTIVO'
        AND ST_DWithin(
                u.coordenada,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                100
            )
        """, nativeQuery = true)
    int contarEnRadio100m(@Param("lat") double lat, @Param("lng") double lng);

    Optional<Ubicacion> findByVendedorIdAndActivoTrue(Integer vendedorId);

    // Obtener todos los puntos activos con su conteo de vecinos en radio
    @Query(value = """
        SELECT
            ST_Y(u.coordenada::geometry)  AS lat,
            ST_X(u.coordenada::geometry)  AS lng,
            COUNT(*) OVER (
                PARTITION BY ST_SnapToGrid(
                    u.coordenada::geometry, :radio / 111320.0
                )
            ) AS count
        FROM ubicaciones u
        JOIN vendedores v ON v.id = u.vendedor_id
        WHERE u.activo = true
        AND v.estado = 'ACTIVO'
        AND v.visible = true
        """, nativeQuery = true)
    List<Object[]> findPuntosActivosConDensidad(@Param("radio") double radio);
}
