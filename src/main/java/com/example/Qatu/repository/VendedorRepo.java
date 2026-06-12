package com.example.Qatu.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;

public interface VendedorRepo extends GenericRepo<Vendedor, Integer> {
    // Verificar si el email ya existe para evitar duplicados
    boolean existsByEmail(String email);

    // Buscar por email para autenticación
    Optional<Vendedor> findByEmail(String email);

    // Vendedores por estado (ACTIVO, INACTIVO, SUSPENDIDO)
    Page<Vendedor> findByEstado(EstadoVendedor estado, Pageable pageable);

    // Vendedores ACTIVOS y visibles en el mapa
    Page<Vendedor> findByEstadoAndVisibleTrue(EstadoVendedor estado, Pageable pageable);

    Page<Vendedor> findAll(Pageable pageable);

}
