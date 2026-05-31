package com.example.Qatu.service;

import java.util.List;

import com.example.Qatu.dto.VendedorMapaDTO;
import com.example.Qatu.dto.VendedorPerfilDTO;
import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.models.Vendedor;

public interface IVendedorService extends IGenericService<Vendedor, Integer> {
    // Métodos específicos para Vendedor
    Vendedor registrarVendedor(VendedorRegisterDTO dto);
    VendedorResponseDTO cambiarVisibilidad(Integer vendedorId, Boolean visible);
    void actualizarFcmToken(Integer vendedorId, String fcmToken);
    List<VendedorMapaDTO> listarVendedoresActivosEnMapa(String categoria);
    VendedorPerfilDTO obtenerPerfil(Integer vendedorId);
}
