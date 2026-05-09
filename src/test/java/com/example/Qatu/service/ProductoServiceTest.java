package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Qatu.dto.ProductoRegisterDTO;
import com.example.Qatu.dto.ProductoResponseDTO;
import com.example.Qatu.dto.ProductoUpdateDTO;
import com.example.Qatu.mapper.ProductoMapper;
import com.example.Qatu.models.Producto;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.ProductoRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.ProductoService;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock private ProductoRepo productoRepo;
    @Mock private VendedorRepo vendedorRepo;
    @Mock private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    private Vendedor vendedor;
    private Producto producto;
    private ProductoRegisterDTO registerDTO;
    private ProductoUpdateDTO updateDTO;
    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setId(1);
        vendedor.setNombre("Carlos Quispe");
        vendedor.setEstado(EstadoVendedor.ACTIVO);

        producto = new Producto();
        producto.setId(1);
        producto.setNombre("Jugo de maracuyá");
        producto.setPrecio(2.50);
        producto.setDescripcion("Jugo natural sin azúcar");
        producto.setFotoUrl("https://ejemplo.com/foto.jpg");
        producto.setActivo(true);
        producto.setVendedor(vendedor);

        registerDTO = new ProductoRegisterDTO();
        registerDTO.setNombre("Jugo de maracuyá");
        registerDTO.setPrecio(2.50);
        registerDTO.setDescripcion("Jugo natural sin azúcar");
        registerDTO.setFotoUrl("https://ejemplo.com/foto.jpg");

        updateDTO = new ProductoUpdateDTO();
        updateDTO.setNombre("Jugo de maracuyá premium");
        updateDTO.setPrecio(3.00);
        updateDTO.setDescripcion("Jugo natural con hielo");
        updateDTO.setFotoUrl("https://ejemplo.com/foto2.jpg");

        responseDTO = new ProductoResponseDTO();
        responseDTO.setId(1);
        responseDTO.setNombre("Jugo de maracuyá");
        responseDTO.setPrecio(2.50);
        responseDTO.setVendedorId(1);
        responseDTO.setNombreVendedor("Carlos Quispe");
    }

    // ══ listarProductosActivos ════════════════════════════════════════════════

    @Test
    @DisplayName("Lista productos activos de un vendedor correctamente")
    void listarProductos_exitoso() {
        when(productoRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(List.of(producto));
        when(productoMapper.toResponseDTO(producto))
            .thenReturn(responseDTO);

        List<ProductoResponseDTO> resultado =
            productoService.listarProductosActivos(1);

        assertEquals(1, resultado.size());
        assertEquals("Jugo de maracuyá", resultado.get(0).getNombre());
        verify(productoRepo, times(1)).findByVendedorIdAndActivoTrue(1);
    }

    @Test
    @DisplayName("Devuelve lista vacía si el vendedor no tiene productos activos")
    void listarProductos_listaVacia() {
        when(productoRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(List.of());

        List<ProductoResponseDTO> resultado =
            productoService.listarProductosActivos(1);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("No devuelve productos inactivos en el listado público")
    void listarProductos_noDevuelveInactivos() {
        Producto inactivo = new Producto();
        inactivo.setId(2);
        inactivo.setActivo(false);
        inactivo.setVendedor(vendedor);

        // findByVendedorIdAndActivoTrue solo devuelve activos — inactivo no aparece
        when(productoRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(List.of(producto)); // solo el activo

        List<ProductoResponseDTO> resultado =
            productoService.listarProductosActivos(1);

        assertEquals(1, resultado.size());
        verify(productoRepo, never()).findByVendedorId(any());
    }

    // ══ crearProducto ═════════════════════════════════════════════════════════

    @Test
    @DisplayName("Crea un producto correctamente para vendedor ACTIVO")
    void crearProducto_exitoso() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(productoMapper.toEntity(registerDTO)).thenReturn(producto);
        when(productoRepo.save(any())).thenReturn(producto);
        when(productoMapper.toResponseDTO(producto)).thenReturn(responseDTO);

        ProductoResponseDTO resultado =
            productoService.crearProducto(1, registerDTO);

        assertNotNull(resultado);
        assertEquals("Jugo de maracuyá", resultado.getNombre());
        assertEquals(1, resultado.getVendedorId());
        verify(productoRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("El vendedor se asigna correctamente al crear el producto")
    void crearProducto_asignaVendedor() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(productoMapper.toEntity(registerDTO)).thenReturn(producto);
        when(productoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(productoMapper.toResponseDTO(any())).thenReturn(responseDTO);

        productoService.crearProducto(1, registerDTO);

        // Verificar que el vendedor fue asignado al producto antes de guardar
        verify(productoRepo).save(argThat(p ->
            p.getVendedor() != null &&
            p.getVendedor().getId().equals(1)
        ));
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no existe al crear producto")
    void crearProducto_vendedorNoEncontrado() {
        when(vendedorRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.crearProducto(99, registerDTO)
        );

        assertEquals("Vendedor no encontrado", ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor está PENDIENTE")
    void crearProducto_vendedorPendiente() {
        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.crearProducto(1, registerDTO)
        );

        assertEquals("Tu cuenta debe estar activa para publicar productos",
            ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor está SUSPENDIDO")
    void crearProducto_vendedorSuspendido() {
        vendedor.setEstado(EstadoVendedor.SUSPENDIDO);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.crearProducto(1, registerDTO)
        );

        assertEquals("Tu cuenta debe estar activa para publicar productos",
            ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    // ══ actualizarProducto ════════════════════════════════════════════════════

    @Test
    @DisplayName("Actualiza un producto correctamente")
    void actualizarProducto_exitoso() {
        when(productoRepo.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepo.save(any())).thenReturn(producto);
        when(productoMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ProductoResponseDTO resultado =
            productoService.actualizarProducto(1, 1, updateDTO);

        assertNotNull(resultado);
        assertEquals("Jugo de maracuyá premium", producto.getNombre());
        assertEquals(3.00, producto.getPrecio());
        assertEquals("Jugo natural con hielo", producto.getDescripcion());
        verify(productoRepo, times(1)).save(producto);
    }

    @Test
    @DisplayName("Lanza excepción si el producto no existe al actualizar")
    void actualizarProducto_noEncontrado() {
        when(productoRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.actualizarProducto(1, 99, updateDTO)
        );

        assertEquals("Producto no encontrado", ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el producto no pertenece al vendedor")
    void actualizarProducto_noEsDueno() {
        // El producto pertenece al vendedor 1
        // El vendedor 2 intenta editarlo
        Vendedor otroVendedor = new Vendedor();
        otroVendedor.setId(2);
        producto.setVendedor(otroVendedor);

        when(productoRepo.findById(1)).thenReturn(Optional.of(producto));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.actualizarProducto(1, 1, updateDTO)
        );

        assertEquals("No tienes permiso para editar este producto",
            ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    // ══ eliminarProducto ══════════════════════════════════════════════════════

    @Test
    @DisplayName("Elimina (desactiva) un producto correctamente — soft delete")
    void eliminarProducto_exitoso() {
        when(productoRepo.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepo.save(any())).thenReturn(producto);

        productoService.eliminarProducto(1, 1);

        // Verifica soft delete — activo = false, no se elimina físicamente
        assertFalse(producto.getActivo());
        verify(productoRepo, times(1)).save(producto);
        verify(productoRepo, never()).delete(any());
        verify(productoRepo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Lanza excepción si el producto no existe al eliminar")
    void eliminarProducto_noEncontrado() {
        when(productoRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.eliminarProducto(1, 99)
        );

        assertEquals("Producto no encontrado", ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el producto no pertenece al vendedor al eliminar")
    void eliminarProducto_noEsDueno() {
        Vendedor otroVendedor = new Vendedor();
        otroVendedor.setId(2);
        producto.setVendedor(otroVendedor);

        when(productoRepo.findById(1)).thenReturn(Optional.of(producto));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> productoService.eliminarProducto(1, 1)
        );

        assertEquals("No tienes permiso para eliminar este producto",
            ex.getMessage());
        verify(productoRepo, never()).save(any());
    }

    @Test
    @DisplayName("El producto eliminado no aparece en el listado público")
    void eliminarProducto_noApareceEnListado() {
        when(productoRepo.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepo.save(any())).thenReturn(producto);

        productoService.eliminarProducto(1, 1);

        // Después del soft delete, findByVendedorIdAndActivoTrue no lo devuelve
        when(productoRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(List.of()); // ya no aparece

        List<ProductoResponseDTO> resultado =
            productoService.listarProductosActivos(1);

        assertTrue(resultado.isEmpty());
    }
}