package com.example.Qatu.service;


import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ReporteRequestDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.models.Reporte;

public interface IReporteService extends IGenericService<Reporte, Integer> {
    ReporteResponseDTO crearReporte(Integer vendedorId, ReporteRequestDTO dto);
    PaginaResponseDTO<ReporteResponseDTO> listarMisReportes(Integer vendedorId, int pagina, int tamanio);
}
