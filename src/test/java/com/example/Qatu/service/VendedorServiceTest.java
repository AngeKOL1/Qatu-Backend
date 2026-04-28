package com.example.Qatu.service;

import com.example.Qatu.dto.VendedorRegisterDTO;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Categoria;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.models.enums.Movilidad;
import com.example.Qatu.repository.CategoriaRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.VendedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendedorServiceTest {

    @Mock
    private VendedorRepo vendedorRepo;

    @Mock
    private CategoriaRepo categoriaRepo;

    @Mock
    private VendedorMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private VendedorService vendedorService;

    private VendedorRegisterDTO dto;
    private Categoria categoria;
    private Vendedor vendedor;

    @BeforeEach
    void setUp() {
        // DTO de prueba
        dto = new VendedorRegisterDTO();
        dto.setNombre("AngeloFrutas");
        dto.setEmail("angelo@hotmail.com");
        dto.setPassword("450504504");
        dto.setDni("910293847");
        dto.setTelefono("981234567");
        dto.setDescripcion("Venta de frutas");
        dto.setTipoMovilidad(Movilidad.CARRITO);
        dto.setHorarioInicio(LocalTime.of(7, 0));
        dto.setHorarioFin(LocalTime.of(18, 0));
        dto.setNombreCategoria("COMIDA");

        // Categoría simulada
        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("COMIDA");

        // Vendedor simulado (resultado del mapper)
        vendedor = new Vendedor();
        vendedor.setNombre("AngeloFrutas");
        vendedor.setEmail("angelo@hotmail.com");
        vendedor.setPassword("$2a$10$hashedpassword");
        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        vendedor.setCategoria(categoria);
    }

    // ── CASO 1: Registro exitoso ──────────────────────────────────────────────
    @Test
    @DisplayName("Registro exitoso de vendedor con datos válidos")
    void registrarVendedor_exitoso() {

        // Arrange — simulamos el comportamiento de los mocks
        when(vendedorRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(categoriaRepo.findByNombre("COMIDA")).thenReturn(Optional.of(categoria));
        when(mapper.toVendedor(dto)).thenReturn(vendedor);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("$2a$10$hashedpassword");
        when(vendedorRepo.save(any(Vendedor.class))).thenReturn(vendedor);

        // Act
        Vendedor resultado = vendedorService.registrarVendedor(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("AngeloFrutas", resultado.getNombre());
        assertEquals("angelo@hotmail.com", resultado.getEmail());
        assertEquals(EstadoVendedor.PENDIENTE, resultado.getEstado());
        assertEquals("COMIDA", resultado.getCategoria().getNombre());

        // Verificar que se llamaron los métodos correctos
        verify(vendedorRepo, times(1)).existsByEmail(dto.getEmail());
        verify(categoriaRepo, times(1)).findByNombre("COMIDA");
        verify(passwordEncoder, times(1)).encode(dto.getPassword());
        verify(vendedorRepo, times(1)).save(any(Vendedor.class));
    }

    // ── CASO 2: Email duplicado ───────────────────────────────────────────────
    @Test
    @DisplayName("Lanza excepción si el email ya está registrado")
    void registrarVendedor_emailDuplicado() {

        // Arrange
        when(vendedorRepo.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> vendedorService.registrarVendedor(dto)
        );

        assertEquals("El email ya está registrado", ex.getMessage());

        // Verificar que nunca se llegó a guardar
        verify(vendedorRepo, never()).save(any());
        verify(categoriaRepo, never()).findByNombre(anyString());
    }

    // ── CASO 3: Categoría no encontrada ──────────────────────────────────────
    @Test
    @DisplayName("Lanza excepción si la categoría no existe")
    void registrarVendedor_categoriaNoEncontrada() {

        // Arrange
        when(vendedorRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(categoriaRepo.findByNombre("COMIDA")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> vendedorService.registrarVendedor(dto)
        );

        assertEquals("Categoría no encontrada", ex.getMessage());
        verify(vendedorRepo, never()).save(any());
    }

    // ── CASO 4: La contraseña se encripta ────────────────────────────────────
    @Test
    @DisplayName("La contraseña se guarda encriptada con BCrypt")
    void registrarVendedor_passwordEncriptada() {

        // Arrange
        when(vendedorRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(categoriaRepo.findByNombre("COMIDA")).thenReturn(Optional.of(categoria));
        when(mapper.toVendedor(dto)).thenReturn(vendedor);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("$2a$10$hashedpassword");
        when(vendedorRepo.save(any())).thenReturn(vendedor);

        // Act
        vendedorService.registrarVendedor(dto);

        // Assert — la contraseña en texto plano nunca se guarda
        verify(passwordEncoder, times(1)).encode("450504504");
        assertNotEquals("450504504", vendedor.getPassword());
    }

    // ── CASO 5: Estado inicial es PENDIENTE ───────────────────────────────────
    @Test
    @DisplayName("El vendedor se crea con estado PENDIENTE")
    void registrarVendedor_estadoPendiente() {

        // Arrange
        when(vendedorRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        when(categoriaRepo.findByNombre("COMIDA")).thenReturn(Optional.of(categoria));
        when(mapper.toVendedor(dto)).thenReturn(vendedor);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(vendedorRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Vendedor resultado = vendedorService.registrarVendedor(dto);

        // Assert
        assertEquals(EstadoVendedor.PENDIENTE, resultado.getEstado());
    }
}