package com.example.Qatu.service;

import com.example.Qatu.dto.PaginaResponseDTO;
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

    PaginaResponseDTO<VendedorMapaDTO> listarVendedoresActivosEnMapa(
            String categoria, int pagina, int tamanio);

    VendedorPerfilDTO obtenerPerfil(Integer vendedorId, int pagina, int tamanio);
}
