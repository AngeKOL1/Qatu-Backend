package com.example.Qatu.service;

import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ProductoRegisterDTO;
import com.example.Qatu.dto.ProductoResponseDTO;
import com.example.Qatu.dto.ProductoUpdateDTO;
import com.example.Qatu.models.Producto;

public interface IProductoService extends IGenericService<Producto, Integer> {
    // Público — cualquier usuario puede ver el catálogo
    PaginaResponseDTO<ProductoResponseDTO> listarProductosActivos(Integer vendedorId, int pagina, int tamanio);

    // Solo el vendedor autenticado
    ProductoResponseDTO crearProducto(Integer vendedorId, ProductoRegisterDTO dto);

    ProductoResponseDTO actualizarProducto(Integer vendedorId, Integer productoId, ProductoUpdateDTO dto);

    void eliminarProducto(Integer vendedorId, Integer productoId);
}
