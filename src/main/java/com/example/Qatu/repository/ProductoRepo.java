package com.example.Qatu.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.Qatu.models.Producto;

public interface ProductoRepo extends GenericRepo<Producto, Integer> {
     // Todos los productos activos de un vendedor
    Page<Producto> findByVendedorIdAndActivoTrue(Integer vendedorId, Pageable pageable);

    // Todos los productos de un vendedor (activos e inactivos)
    List<Producto> findByVendedorId(Integer vendedorId);
}
