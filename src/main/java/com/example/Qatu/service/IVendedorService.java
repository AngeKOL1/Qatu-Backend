package com.example.Qatu.service;

import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.models.Vendedor;

public interface IVendedorService extends IGenericService<Vendedor, Integer> {
    // Métodos específicos para Vendedor
    Vendedor registrarVendedor(VendedorRegisterDTO dto);
}
