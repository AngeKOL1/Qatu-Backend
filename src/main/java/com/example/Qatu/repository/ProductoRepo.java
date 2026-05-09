package com.example.Qatu.repository;

import java.util.List;

import com.example.Qatu.models.Producto;

public interface ProductoRepo extends GenericRepo<Producto, Integer> {
     // Todos los productos activos de un vendedor
    List<Producto> findByVendedorIdAndActivoTrue(Integer vendedorId);

    // Todos los productos de un vendedor (activos e inactivos)
    List<Producto> findByVendedorId(Integer vendedorId);
}
