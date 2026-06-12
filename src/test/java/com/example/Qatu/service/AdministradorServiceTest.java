package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ReporteMapper;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Reporte;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.AdministradorRepo;
import com.example.Qatu.repository.ReporteRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.AdministradorService;
import com.example.Qatu.service.impl.WebSocketService;

@ExtendWith(MockitoExtension.class)
class AdministradorServiceTest {

    @Mock
    private VendedorRepo vendedorRepo;
    @Mock
    private VendedorMapper vendedorMapper;
    @Mock
    private ReporteRepo reporteRepo;
    @Mock
    private ReporteMapper reporteMapper;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private AdministradorRepo repo;

    @InjectMocks
    private AdministradorService administradorService;

    private Vendedor vendedor;
    private Reporte reporte;
    private VendedorResponseDTO vendedorResponseDTO;
    private ReporteResponseDTO reporteResponseDTO;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setId(1);
        vendedor.setNombre("Carlos Quispe");
        vendedor.setEmail("carlos@email.com");
        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        vendedor.setVisible(false);

        vendedorResponseDTO = new VendedorResponseDTO();
        vendedorResponseDTO.setId(1);
        vendedorResponseDTO.setNombre("Carlos Quispe");

        reporte = new Reporte();
        reporte.setId(1);
        reporte.setAsunto("No puedo subir fotos");
        reporte.setDescripcion("Cuando intento subir una foto aparece error.");
        reporte.setEstado("ABIERTO");
        reporte.setVendedor(vendedor);

        reporteResponseDTO = new ReporteResponseDTO();
        reporteResponseDTO.setId(1);
        reporteResponseDTO.setAsunto("No puedo subir fotos");
        reporteResponseDTO.setEstado("ABIERTO");
    }

    // ══ listarVendedores ══════════════════════════════════════════════════════

    @Test
    @DisplayName("Lista vendedores filtrando por estado PENDIENTE")
    void listarVendedores_conEstado() {
        Page<Vendedor> page = new PageImpl<>(List.of(vendedor));
        when(vendedorRepo.findByEstado(eq(EstadoVendedor.PENDIENTE), any(Pageable.class)))
                .thenReturn(page);
        when(vendedorMapper.toResponseDTO(vendedor)).thenReturn(vendedorResponseDTO);

        PaginaResponseDTO<VendedorResponseDTO> resultado = administradorService
                .listarVendedores(EstadoVendedor.PENDIENTE, 0, 20);

        assertEquals(1, resultado.getContenido().size());
        assertEquals("Carlos Quispe", resultado.getContenido().get(0).getNombre());
        verify(vendedorRepo, times(1))
                .findByEstado(eq(EstadoVendedor.PENDIENTE), any(Pageable.class));
    }

    @Test
    @DisplayName("Lista todos los vendedores cuando el estado es null")
    void listarVendedores_sinEstado() {
        Page<Vendedor> page = new PageImpl<>(List.of(vendedor));
        when(vendedorRepo.findAll(any(Pageable.class))).thenReturn(page);
        when(vendedorMapper.toResponseDTO(vendedor)).thenReturn(vendedorResponseDTO);

        PaginaResponseDTO<VendedorResponseDTO> resultado = administradorService.listarVendedores(null, 0, 20);

        assertEquals(1, resultado.getContenido().size());
        verify(vendedorRepo, times(1)).findAll(any(Pageable.class));
        verify(vendedorRepo, never()).findByEstado(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Devuelve lista vacía si no hay vendedores con ese estado")
    void listarVendedores_listaVacia() {
        Page<Vendedor> page = new PageImpl<>(List.of());
        when(vendedorRepo.findByEstado(eq(EstadoVendedor.SUSPENDIDO), any(Pageable.class)))
                .thenReturn(page);

        PaginaResponseDTO<VendedorResponseDTO> resultado = administradorService
                .listarVendedores(EstadoVendedor.SUSPENDIDO, 0, 20);

        assertTrue(resultado.getContenido().isEmpty());
    }

    // ══ cambiarEstadoVendedor ═════════════════════════════════════════════════

    @Test
    @DisplayName("Aprueba un vendedor PENDIENTE → ACTIVO correctamente")
    void cambiarEstado_pendienteAActivo() {
        vendedor.setEstado(EstadoVendedor.PENDIENTE);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(vendedorRepo.save(any())).thenReturn(vendedor);
        when(vendedorMapper.toResponseDTO(any())).thenReturn(vendedorResponseDTO);

        VendedorResponseDTO resultado = administradorService.cambiarEstadoVendedor(1, EstadoVendedor.ACTIVO);

        assertNotNull(resultado);
        assertEquals(EstadoVendedor.ACTIVO, vendedor.getEstado());
        verify(vendedorRepo, times(1)).save(vendedor);
        // No debe emitir WebSocket al aprobar
        verify(webSocketService, never()).emitirVendedorInactivo(any());
    }

    @Test
    @DisplayName("Suspende un vendedor ACTIVO → lo saca del mapa y emite WebSocket")
    void cambiarEstado_activoASuspendido() {
        vendedor.setEstado(EstadoVendedor.ACTIVO);
        vendedor.setVisible(true);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(vendedorRepo.save(any())).thenReturn(vendedor);
        when(vendedorMapper.toResponseDTO(any())).thenReturn(vendedorResponseDTO);

        administradorService.cambiarEstadoVendedor(1, EstadoVendedor.SUSPENDIDO);

        assertEquals(EstadoVendedor.SUSPENDIDO, vendedor.getEstado());
        assertFalse(vendedor.getVisible());
        // Debe emitir WebSocket para sacar el pin del mapa
        verify(webSocketService, times(1)).emitirVendedorInactivo(1);
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no existe")
    void cambiarEstado_vendedorNoEncontrado() {
        when(vendedorRepo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
                ModelNotFoundException.class,
                () -> administradorService.cambiarEstadoVendedor(99, EstadoVendedor.ACTIVO));

        assertEquals("Vendedor no encontrado", ex.getMessage());
        verify(vendedorRepo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción al intentar pasar de PENDIENTE a SUSPENDIDO")
    void cambiarEstado_pendienteASuspendido_invalido() {
        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> administradorService.cambiarEstadoVendedor(1, EstadoVendedor.SUSPENDIDO));

        assertEquals("No se puede cambiar de PENDIENTE a SUSPENDIDO", ex.getMessage());
        verify(vendedorRepo, never()).save(any());
        verify(webSocketService, never()).emitirVendedorInactivo(any());
    }

    @Test
    @DisplayName("Al suspender un vendedor visible = false")
    void cambiarEstado_suspendido_quitaVisibilidad() {
        vendedor.setEstado(EstadoVendedor.ACTIVO);
        vendedor.setVisible(true);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(vendedorRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(vendedorMapper.toResponseDTO(any())).thenReturn(vendedorResponseDTO);

        administradorService.cambiarEstadoVendedor(1, EstadoVendedor.SUSPENDIDO);

        assertFalse(vendedor.getVisible());
    }

    // ══ listarReportes ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Lista reportes filtrando por estado ABIERTO")
    void listarReportes_conEstado() {
        Page<Reporte> page = new PageImpl<>(List.of(reporte));
        when(reporteRepo.findByEstado(eq("ABIERTO"), any(Pageable.class)))
                .thenReturn(page);
        when(reporteMapper.toResponseDTO(reporte)).thenReturn(reporteResponseDTO);

        PaginaResponseDTO<ReporteResponseDTO> resultado = administradorService.listarReportes("ABIERTO", 0, 20);

        assertEquals(1, resultado.getContenido().size());
        assertEquals("ABIERTO", resultado.getContenido().get(0).getEstado());
        verify(reporteRepo, times(1))
                .findByEstado(eq("ABIERTO"), any(Pageable.class));
    }

    @Test
    @DisplayName("Lista todos los reportes cuando el estado es null")
    void listarReportes_sinEstado() {
        Page<Reporte> page = new PageImpl<>(List.of(reporte));
        when(reporteRepo.findAll(any(Pageable.class))).thenReturn(page);
        when(reporteMapper.toResponseDTO(reporte)).thenReturn(reporteResponseDTO);

        PaginaResponseDTO<ReporteResponseDTO> resultado = administradorService.listarReportes(null, 0, 20);

        assertEquals(1, resultado.getContenido().size());
        verify(reporteRepo, times(1)).findAll(any(Pageable.class));
        verify(reporteRepo, never()).findByEstado(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("Devuelve lista vacía si no hay reportes con ese estado")
    void listarReportes_listaVacia() {
        Page<Reporte> page = new PageImpl<>(List.of());
        when(reporteRepo.findByEstado(eq("CERRADO"), any(Pageable.class)))
                .thenReturn(page);

        PaginaResponseDTO<ReporteResponseDTO> resultado = administradorService.listarReportes("CERRADO", 0, 20);

        assertTrue(resultado.getContenido().isEmpty());
    }

    // ══ actualizarReporte ═════════════════════════════════════════════════════

    @Test
    @DisplayName("Actualiza estado y respuesta del reporte correctamente")
    void actualizarReporte_exitoso() {
        when(reporteRepo.findById(1)).thenReturn(Optional.of(reporte));
        when(reporteRepo.save(any())).thenReturn(reporte);
        when(reporteMapper.toResponseDTO(any())).thenReturn(reporteResponseDTO);

        ReporteResponseDTO resultado = administradorService.actualizarReporte(1, "CERRADO", "Problema resuelto.");

        assertEquals("CERRADO", reporte.getEstado());
        assertEquals("Problema resuelto.", reporte.getRespuesta());
        verify(reporteRepo, times(1)).save(reporte);
    }

    @Test
    @DisplayName("Actualiza solo el estado si la respuesta es null")
    void actualizarReporte_sinRespuesta() {
        when(reporteRepo.findById(1)).thenReturn(Optional.of(reporte));
        when(reporteRepo.save(any())).thenReturn(reporte);
        when(reporteMapper.toResponseDTO(any())).thenReturn(reporteResponseDTO);

        administradorService.actualizarReporte(1, "EN_REVISION", null);

        assertEquals("EN_REVISION", reporte.getEstado());
        // La respuesta no se toca si es null
        assertNull(reporte.getRespuesta());
        verify(reporteRepo, times(1)).save(reporte);
    }

    @Test
    @DisplayName("Actualiza solo el estado si la respuesta está vacía")
    void actualizarReporte_respuestaVacia() {
        when(reporteRepo.findById(1)).thenReturn(Optional.of(reporte));
        when(reporteRepo.save(any())).thenReturn(reporte);
        when(reporteMapper.toResponseDTO(any())).thenReturn(reporteResponseDTO);

        administradorService.actualizarReporte(1, "EN_REVISION", "   ");

        assertEquals("EN_REVISION", reporte.getEstado());
        assertNull(reporte.getRespuesta());
        verify(reporteRepo, times(1)).save(reporte);
    }

    @Test
    @DisplayName("Lanza excepción si el reporte no existe")
    void actualizarReporte_noEncontrado() {
        when(reporteRepo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
                ModelNotFoundException.class,
                () -> administradorService.actualizarReporte(99, "CERRADO", "Respuesta"));

        assertEquals("Reporte no encontrado", ex.getMessage());
        verify(reporteRepo, never()).save(any());
    }
}