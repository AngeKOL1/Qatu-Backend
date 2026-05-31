package com.example.Qatu.repository;

import java.util.List;
import java.util.Optional;

import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;

public interface VendedorRepo extends GenericRepo<Vendedor, Integer> {
    // Verificar si el email ya existe para evitar duplicados
    boolean existsByEmail(String email);

    // Buscar por email para autenticación
    Optional<Vendedor> findByEmail(String email);

    // Vendedores por estado (ACTIVO, INACTIVO, SUSPENDIDO)
    List<Vendedor> findByEstado(EstadoVendedor estado);

    // Vendedores ACTIVOS y visibles en el mapa
    List<Vendedor> findByEstadoAndVisibleTrue(EstadoVendedor estado);

}
