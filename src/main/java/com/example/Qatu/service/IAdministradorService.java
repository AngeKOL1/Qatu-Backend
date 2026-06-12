package com.example.Qatu.service;


import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.models.Administrador;
import com.example.Qatu.models.enums.EstadoVendedor;

public interface IAdministradorService extends IGenericService<Administrador, Integer> {
    PaginaResponseDTO<VendedorResponseDTO> listarVendedores(EstadoVendedor estado, int pagina, int tamanio);

    VendedorResponseDTO cambiarEstadoVendedor(Integer vendedorId, EstadoVendedor estado);

    PaginaResponseDTO<ReporteResponseDTO> listarReportes(String estado, int pagina, int tamanio);

    ReporteResponseDTO actualizarReporte(Integer reporteId, String estado, String respuesta);
}
