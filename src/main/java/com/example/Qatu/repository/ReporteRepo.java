package com.example.Qatu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Qatu.models.Reporte;

public interface ReporteRepo extends GenericRepo<Reporte, Integer> {
    Page<Reporte> findByEstado(String estado, Pageable pageable);

    Page<Reporte> findByVendedorId(Integer vendedorId, Pageable pageable);
}
