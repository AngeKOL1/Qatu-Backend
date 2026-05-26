package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

import com.example.Qatu.config.UmbralConfig;
import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.models.enums.Movilidad;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.HeatmapService;
import com.example.Qatu.service.impl.SugerenciaDeReasignacionService;
import com.example.Qatu.service.impl.UbicacionService;
import com.example.Qatu.util.GeoUtils;

@ExtendWith(MockitoExtension.class)
class UbicacionServiceTest {

    @Mock private UbicacionRepo repo;           // ← para el GenericService
    @Mock private VendedorRepo vendedorRepo;
    @Mock private IWebSoketSevice webSocketService;  // ← interfaz, no implementación
    @Mock private HeatmapService heatmapService;
    @Mock private SugerenciaDeReasignacionService sugerenciaService;
    @Mock private UmbralConfig umbralConfig;

    @InjectMocks
    private UbicacionService ubicacionService;

    private Vendedor vendedor;
    private UbicacionRequestDTO dto;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setId(1);
        vendedor.setNombre("Carlos");
        vendedor.setEstado(EstadoVendedor.ACTIVO);
        vendedor.setVisible(true);
        vendedor.setTipoMovilidad(Movilidad.CARRITO);

        dto = new UbicacionRequestDTO();
        dto.setLat(-7.1638);
        dto.setLng(-78.5001);
    }

    @Test
    @DisplayName("Actualiza ubicación correctamente cuando el vendedor está activo")
    void actualizarUbicacion_exitoso() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        Ubicacion ubicacionGuardada = new Ubicacion();
        ubicacionGuardada.setId(10);
        ubicacionGuardada.setVendedor(vendedor);
        ubicacionGuardada.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        ubicacionGuardada.setTimestamp(LocalDateTime.now());
        ubicacionGuardada.setActivo(true);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(repo).desactivarPorVendedor(1);
        when(repo.save(any(Ubicacion.class))).thenReturn(ubicacionGuardada);
        when(repo.contarEnRadio100m(anyDouble(), anyDouble())).thenReturn(3);

        UbicacionResponseDTO response = ubicacionService.actualizarUbicacion(1, dto);

        assertNotNull(response);
        assertEquals(10, response.getUbicacionId());
        assertEquals(1, response.getVendedorId());
        assertEquals(-7.1638, response.getLat());
        assertEquals(-78.5001, response.getLng());

        verify(repo, times(1)).desactivarPorVendedor(1);
        verify(repo, times(1)).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no existe")
    void actualizarUbicacion_vendedorNoEncontrado() {
        when(vendedorRepo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> ubicacionService.actualizarUbicacion(99, dto)
        );

        assertEquals("Vendedor no encontrado", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no está activo")
    void actualizarUbicacion_vendedorNoActivo() {
        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> ubicacionService.actualizarUbicacion(1, dto)
        );

        assertEquals("El vendedor no está activo", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Desactiva la ubicación anterior antes de guardar la nueva")
    void actualizarUbicacion_desactivaAnterior() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        Ubicacion nueva = new Ubicacion();
        nueva.setId(11);
        nueva.setVendedor(vendedor);
        nueva.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        nueva.setTimestamp(LocalDateTime.now());

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(repo).desactivarPorVendedor(1);
        when(repo.save(any())).thenReturn(nueva);
        when(repo.contarEnRadio100m(anyDouble(), anyDouble())).thenReturn(3);

        ubicacionService.actualizarUbicacion(1, dto);

        InOrder orden = inOrder(repo);
        orden.verify(repo).desactivarPorVendedor(1);
        orden.verify(repo).save(any(Ubicacion.class));
    }

    @Test
    @DisplayName("Las coordenadas del Point se guardan con el orden correcto (lng, lat)")
    void actualizarUbicacion_coordenadasCorrectas() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(repo).desactivarPorVendedor(1);
        when(repo.contarEnRadio100m(anyDouble(), anyDouble())).thenReturn(3);

        when(repo.save(any(Ubicacion.class))).thenAnswer(invocation -> {
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

    @Test
    @DisplayName("Emite evento WebSocket después de actualizar ubicación")
    void actualizarUbicacion_emiteWebSocket() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        Ubicacion guardada = new Ubicacion();
        guardada.setId(10);
        guardada.setVendedor(vendedor);
        guardada.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        guardada.setTimestamp(LocalDateTime.now());

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(repo).desactivarPorVendedor(1);
        when(repo.save(any())).thenReturn(guardada);
        when(repo.contarEnRadio100m(anyDouble(), anyDouble())).thenReturn(3);

        ubicacionService.actualizarUbicacion(1, dto);

        verify(webSocketService, times(1)).emitirUbicacionActualizada(any());
    }

    @Test
    @DisplayName("Evalúa sugerencia cuando la zona supera el umbral rojo")
    void actualizarUbicacion_evaluaSugerenciaEnZonaRoja() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        Ubicacion guardada = new Ubicacion();
        guardada.setId(10);
        guardada.setVendedor(vendedor);
        guardada.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        guardada.setTimestamp(LocalDateTime.now());

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(repo).desactivarPorVendedor(1);
        when(repo.save(any())).thenReturn(guardada);
        when(repo.contarEnRadio100m(anyDouble(), anyDouble())).thenReturn(12);

        ubicacionService.actualizarUbicacion(1, dto);

        verify(sugerenciaService, times(1))
            .evaluarYEnviarSugerencia(eq(1), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("No evalúa sugerencia cuando la zona no supera el umbral rojo")
    void actualizarUbicacion_noEvaluaSugerenciaZonaVerde() {
        when(umbralConfig.getUmbralRojo()).thenReturn(10);

        Ubicacion guardada = new Ubicacion();
        guardada.setId(10);
        guardada.setVendedor(vendedor);
        guardada.setCoordenada(GeoUtils.crearPunto(-7.1638, -78.5001));
        guardada.setTimestamp(LocalDateTime.now());

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        doNothing().when(repo).desactivarPorVendedor(1);
        when(repo.save(any())).thenReturn(guardada);
        when(repo.contarEnRadio100m(anyDouble(), anyDouble())).thenReturn(3);

        ubicacionService.actualizarUbicacion(1, dto);

        verify(sugerenciaService, never())
            .evaluarYEnviarSugerencia(any(), anyDouble(), anyDouble());
    }
}