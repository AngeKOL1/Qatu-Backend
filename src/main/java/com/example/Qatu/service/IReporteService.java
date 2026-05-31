package com.example.Qatu.service;

import java.util.List;

import com.example.Qatu.dto.ReporteRequestDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.models.Reporte;

public interface IReporteService extends IGenericService<Reporte, Integer> {
    ReporteResponseDTO crearReporte(Integer vendedorId, ReporteRequestDTO dto);
    List<ReporteResponseDTO> listarMisReportes(Integer vendedorId);
}
