package com.example.Qatu.service;

import java.util.List;

import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.models.Administrador;
import com.example.Qatu.models.enums.EstadoVendedor;

public interface IAdministradorService extends IGenericService<Administrador, Integer> {
    List<VendedorResponseDTO> listarVendedores(EstadoVendedor estado);
    VendedorResponseDTO cambiarEstadoVendedor(Integer vendedorId, EstadoVendedor estado);
    List<ReporteResponseDTO> listarReportes (String estado);
    ReporteResponseDTO actualizarReporte(Integer reporteId, String estado, String respuesta);
}
