package com.example.Qatu.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Qatu.models.SugerenciaReasignacion;
import com.example.Qatu.models.enums.EstadoSugerencia;

public interface SugerenciaReasignacionRepo extends GenericRepo<SugerenciaReasignacion, Integer> {
    // Verificar cooldown — última sugerencia enviada al vendedor en X minutos
    Optional<SugerenciaReasignacion> findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(
            Integer vendedorId, EstadoSugerencia estado);

    // Historial de sugerencias de un vendedor
    Page<SugerenciaReasignacion> findByVendedorIdOrderByFechaEnvioDesc(
            Integer vendedorId, Pageable pageable);

    // Verificar si ya existe sugerencia enviada para el mismo vendedor y zona
    boolean existsByVendedorIdAndZonaIdAndEstado(
            Integer vendedorId, Integer zonaId, EstadoSugerencia estado);
}
