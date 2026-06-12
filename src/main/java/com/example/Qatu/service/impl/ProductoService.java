package com.example.Qatu.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ProductoRegisterDTO;
import com.example.Qatu.dto.ProductoResponseDTO;
import com.example.Qatu.dto.ProductoUpdateDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ProductoMapper;
import com.example.Qatu.models.Producto;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.ProductoRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IProductoService;
import com.example.Qatu.util.PaginacionUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoService extends GenericService<Producto, Integer> implements IProductoService {
    private final ProductoRepo repo;
    private final ProductoMapper mapper;
    private final VendedorRepo vendedorRepo;

    @Override
    protected ProductoRepo getRepo() {
        return repo;
    }

    @Override
    public PaginaResponseDTO<ProductoResponseDTO> listarProductosActivos(
            Integer vendedorId, int pagina, int tamanio) {

        Pageable pageable = PageRequest.of(pagina, tamanio,
                Sort.by("fechaCreacion").descending());

        Page<Producto> page = repo
                .findByVendedorIdAndActivoTrue(vendedorId, pageable);

        return PaginacionUtils.construir(
                page.map(mapper::toResponseDTO));
    }

    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(Integer vendedorId, ProductoRegisterDTO dto) {
        Vendedor vendedor = vendedorRepo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

        // Solo vendedores ACTIVOS pueden publicar productos
        if (vendedor.getEstado() != EstadoVendedor.ACTIVO) {
            throw new IllegalArgumentException(
                    "Tu cuenta debe estar activa para publicar productos");
        }

        Producto producto = mapper.toEntity(dto);
        producto.setVendedor(vendedor);

        return mapper.toResponseDTO(repo.save(producto));
    }

    @Override
    public ProductoResponseDTO actualizarProducto(Integer vendedorId, Integer productoId, ProductoUpdateDTO dto) {
        Producto producto = repo.findById(productoId)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado"));

        // Verificar que el producto pertenece al vendedor autenticado
        if (!producto.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException(
                    "No tienes permiso para editar este producto");
        }

        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setFotoUrl(dto.getFotoUrl());

        return mapper.toResponseDTO(repo.save(producto));
    }

    @Override
    @Transactional
    public void eliminarProducto(Integer vendedorId, Integer productoId) {
        Producto producto = repo.findById(productoId)
                .orElseThrow(() -> new ModelNotFoundException("Producto no encontrado"));

        // Verificar que el producto pertenece al vendedor autenticado
        if (!producto.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException(
                    "No tienes permiso para eliminar este producto");
        }

        // Soft delete — no eliminar físicamente
        producto.setActivo(false);
        repo.save(producto);
    }
}
