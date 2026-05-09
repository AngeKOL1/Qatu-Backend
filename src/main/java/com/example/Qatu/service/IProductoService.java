package com.example.Qatu.service;

import java.util.List;

import com.example.Qatu.dto.ProductoRegisterDTO;
import com.example.Qatu.dto.ProductoResponseDTO;
import com.example.Qatu.dto.ProductoUpdateDTO;
import com.example.Qatu.models.Producto;

public interface IProductoService extends IGenericService<Producto, Integer> {
    // Público — cualquier usuario puede ver el catálogo
    List<ProductoResponseDTO> listarProductosActivos(Integer vendedorId);

    // Solo el vendedor autenticado
    ProductoResponseDTO crearProducto(Integer vendedorId, ProductoRegisterDTO dto);
    ProductoResponseDTO actualizarProducto(Integer vendedorId, Integer productoId, ProductoUpdateDTO dto);
    void eliminarProducto(Integer vendedorId, Integer productoId);
}
