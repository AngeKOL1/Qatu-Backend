package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.VendedorService;

@ExtendWith(MockitoExtension.class)
class VendedorVisibilidadTest {

    @Mock
    private VendedorRepo repo;

    @Mock
    private VendedorMapper mapper;

    @InjectMocks
    private VendedorService vendedorService;

    private Vendedor vendedor;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setId(1);
        vendedor.setNombre("Carlos");
        vendedor.setEstado(EstadoVendedor.ACTIVO);
        vendedor.setVisible(false);
    }

    // ── CASO 1: Activar visibilidad ───────────────────────────────────────────
    @Test
    @DisplayName("Vendedor activo puede activar su visibilidad en el mapa")
    void cambiarVisibilidad_activar() {

        when(repo.findById(1)).thenReturn(Optional.of(vendedor));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponseDTO(any())).thenReturn(new VendedorResponseDTO());

        vendedorService.cambiarVisibilidad(1, true);

        assertTrue(vendedor.getVisible());
        verify(repo, times(1)).save(vendedor);
    }

    // ── CASO 2: Desactivar visibilidad ────────────────────────────────────────
    @Test
    @DisplayName("Vendedor puede desactivarse del mapa sin cerrar sesión")
    void cambiarVisibilidad_desactivar() {

        vendedor.setVisible(true);
        when(repo.findById(1)).thenReturn(Optional.of(vendedor));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponseDTO(any())).thenReturn(new VendedorResponseDTO());

        vendedorService.cambiarVisibilidad(1, false);

        assertFalse(vendedor.getVisible());
        verify(repo, times(1)).save(vendedor);
    }

    // ── CASO 3: Cuenta pendiente no puede activarse ───────────────────────────
    @Test
    @DisplayName("Vendedor PENDIENTE no puede aparecer en el mapa")
    void cambiarVisibilidad_cuentaPendiente() {

        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        when(repo.findById(1)).thenReturn(Optional.of(vendedor));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> vendedorService.cambiarVisibilidad(1, true)
        );

        assertTrue(ex.getMessage().contains("no está aprobada"));
        verify(repo, never()).save(any());
    }

    // ── CASO 4: Cuenta suspendida no puede activarse ──────────────────────────
    @Test
    @DisplayName("Vendedor SUSPENDIDO no puede aparecer en el mapa")
    void cambiarVisibilidad_cuentaSuspendida() {

        vendedor.setEstado(EstadoVendedor.SUSPENDIDO);
        when(repo.findById(1)).thenReturn(Optional.of(vendedor));

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> vendedorService.cambiarVisibilidad(1, true)
        );

        assertTrue(ex.getMessage().contains("no está aprobada"));
        verify(repo, never()).save(any());
    }
}