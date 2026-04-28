package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.UbicacionService;
import com.example.Qatu.util.GeoUtils;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock
    private UbicacionRepo ubicacionRepo;

    @Mock
    private VendedorRepo vendedorRepo;

    @InjectMocks
    private UbicacionService ubicacionService; // ← clase concreta

    private Vendedor vendedor;
    private UbicacionRequestDTO dto;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setId(1);
        vendedor.setNombre("Carlos");
        vendedor.setEstado(EstadoVendedor.ACTIVO);
        vendedor.setVisible(true);

        dto = new UbicacionRequestDTO();
        dto.setLat(-7.1638);
        dto.setLng(-78.5001);
    }

    @Test
    @DisplayName("Actualiza ubicación correctamente cuando el vendedor está activo")
    void actualizarUbicacion_exitoso() {

        Ubicacion ubicacionGuardada = new Ubicacion();
        ubicacionGuardada.setId(10);
        ubicacionGuardada.setVendedor(vendedor);
        ubicacionGuardada.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        ubicacionGuardada.setTimestamp(LocalDateTime.now());
        ubicacionGuardada.setActiva(true);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(ubicacionRepo).desactivarPorVendedor(1);
        when(ubicacionRepo.save(any(Ubicacion.class))).thenReturn(ubicacionGuardada);

        UbicacionResponseDTO response = ubicacionService.actualizarUbicacion(1, dto);

        assertNotNull(response);
        assertEquals(10, response.getUbicacionId());
        assertEquals(1, response.getVendedorId());
        assertEquals(-7.1638, response.getLat());
        assertEquals(-78.5001, response.getLng());

        verify(ubicacionRepo, times(1)).desactivarPorVendedor(1);
        verify(ubicacionRepo, times(1)).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no existe")
    void actualizarUbicacion_vendedorNoEncontrado() {

        when(vendedorRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> ubicacionService.actualizarUbicacion(99, dto)
        );

        assertEquals("Vendedor no encontrado", ex.getMessage());
        verify(ubicacionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no está activo")
    void actualizarUbicacion_vendedorNoActivo() {

        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> ubicacionService.actualizarUbicacion(1, dto)
        );

        assertEquals("El vendedor no está activo", ex.getMessage());
        verify(ubicacionRepo, never()).save(any());
    }

    @Test
    @DisplayName("Desactiva la ubicación anterior antes de guardar la nueva")
    void actualizarUbicacion_desactivaAnterior() {

        Ubicacion nueva = new Ubicacion();
        nueva.setId(11);
        nueva.setVendedor(vendedor);
        nueva.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        nueva.setTimestamp(LocalDateTime.now());

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(ubicacionRepo).desactivarPorVendedor(1);
        when(ubicacionRepo.save(any())).thenReturn(nueva);

        ubicacionService.actualizarUbicacion(1, dto);

        InOrder orden = inOrder(ubicacionRepo);
        orden.verify(ubicacionRepo).desactivarPorVendedor(1);
        orden.verify(ubicacionRepo).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("Las coordenadas del Point se guardan con el orden correcto (lng, lat)")
    void actualizarUbicacion_coordenadasCorrectas() {

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(ubicacionRepo).desactivarPorVendedor(1);

        when(ubicacionRepo.save(any(Ubicacion.class))).thenAnswer(invocation -> {
            Ubicacion u = invocation.getArgument(0);

            assertEquals(-78.5001, u.getCoordenada().getX(), 0.0001);
            assertEquals(-7.1638,  u.getCoordenada().getY(), 0.0001);

            u.setId(12);
            u.setTimestamp(LocalDateTime.now());
            return u;
        });

        UbicacionResponseDTO response = ubicacionService.actualizarUbicacion(1, dto);

        assertNotNull(response);
        assertEquals(-7.1638,  response.getLat(), 0.0001);
        assertEquals(-78.5001, response.getLng(), 0.0001);
    }
}