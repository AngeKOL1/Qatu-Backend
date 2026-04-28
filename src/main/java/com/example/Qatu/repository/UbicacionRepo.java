package com.example.Qatu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Qatu.models.Ubicacion;

public interface UbicacionRepo extends GenericRepo<Ubicacion, Integer> {
    // Desactivar ubicación anterior del vendedor
    @Modifying
    @Query("UPDATE Ubicacion u SET u.activa = false WHERE u.vendedor.id = :vendedorId")
    void desactivarPorVendedor(@Param("vendedorId") Integer vendedorId);

    // Contar vendedores activos en radio de 100m (para heatmap)
    @Query(value = """
        SELECT COUNT(*) FROM ubicaciones u
        JOIN vendedores v ON v.id = u.vendedor_id
        WHERE u.activa = true
          AND v.estado = 'ACTIVO'
          AND ST_DWithin(
                u.coordenada,
                ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                100
              )
        """, nativeQuery = true)
    int contarEnRadio100m(@Param("lat") double lat, @Param("lng") double lng);

    // Encontrar ubicación activa de un vendedor
    Optional<Ubicacion> findByVendedorIdAndActivaTrue(Integer vendedorId);
}
