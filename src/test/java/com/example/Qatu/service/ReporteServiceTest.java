package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
import com.example.Qatu.dto.ReporteRequestDTO;
import com.example.Qatu.dto.ReporteResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ReporteMapper;
import com.example.Qatu.models.Reporte;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.ReporteRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.impl.ReporteService;


@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepo repo;
    @Mock
    private VendedorRepo vendedorRepo;
    @Mock
    private ReporteMapper reporteMapper;

    @InjectMocks
    private ReporteService reporteService;

    private Vendedor vendedor;
    private Reporte reporte;
    private ReporteRequestDTO requestDTO;
    private ReporteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        vendedor = new Vendedor();
        vendedor.setId(1);
        vendedor.setNombre("Carlos Quispe");
        vendedor.setEstado(EstadoVendedor.ACTIVO);

        reporte = new Reporte();
        reporte.setId(1);
        reporte.setAsunto("No puedo subir fotos");
        reporte.setDescripcion("Aparece error 413 al subir imagen.");
        reporte.setEstado("ABIERTO");
        reporte.setVendedor(vendedor);

        requestDTO = new ReporteRequestDTO();
        requestDTO.setAsunto("No puedo subir fotos");
        requestDTO.setDescripcion("Aparece error 413 al subir imagen.");

        responseDTO = new ReporteResponseDTO();
        responseDTO.setId(1);
        responseDTO.setAsunto("No puedo subir fotos");
        responseDTO.setEstado("ABIERTO");
        responseDTO.setVendedorId(1);
    }

    // ══ crearReporte ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("Crea un reporte correctamente para vendedor ACTIVO")
    void crearReporte_exitoso() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(repo.save(any(Reporte.class))).thenReturn(reporte);
        when(reporteMapper.toResponseDTO(any())).thenReturn(responseDTO);

        ReporteResponseDTO resultado = reporteService.crearReporte(1, requestDTO);

        assertNotNull(resultado);
        assertEquals("No puedo subir fotos", resultado.getAsunto());
        assertEquals("ABIERTO", resultado.getEstado());
        assertEquals(1, resultado.getVendedorId());
        verify(repo, times(1)).save(any(Reporte.class));
    }

    @Test
    @DisplayName("El vendedor se asigna correctamente al reporte")
    void crearReporte_asignaVendedor() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reporteMapper.toResponseDTO(any())).thenReturn(responseDTO);

        reporteService.crearReporte(1, requestDTO);

        verify(repo).save(argThat(r -> r.getVendedor() != null &&
                r.getVendedor().getId().equals(1)));
    }

    @Test
    @DisplayName("El asunto y descripción se asignan correctamente")
    void crearReporte_asignaDatos() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reporteMapper.toResponseDTO(any())).thenReturn(responseDTO);

        reporteService.crearReporte(1, requestDTO);

        verify(repo).save(argThat(r -> "No puedo subir fotos".equals(r.getAsunto()) &&
                "Aparece error 413 al subir imagen.".equals(r.getDescripcion())));
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no existe")
    void crearReporte_vendedorNoEncontrado() {
        when(vendedorRepo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
                ModelNotFoundException.class,
                () -> reporteService.crearReporte(99, requestDTO));

        assertEquals("Vendedor no encontrado", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor está PENDIENTE")
    void crearReporte_vendedorPendiente() {
        vendedor.setEstado(EstadoVendedor.PENDIENTE);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reporteService.crearReporte(1, requestDTO));

        assertEquals("Tu cuenta debe estar activa para enviar reportes",
                ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor está SUSPENDIDO")
    void crearReporte_vendedorSuspendido() {
        vendedor.setEstado(EstadoVendedor.SUSPENDIDO);
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedor));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reporteService.crearReporte(1, requestDTO));

        assertEquals("Tu cuenta debe estar activa para enviar reportes",
                ex.getMessage());
        verify(repo, never()).save(any());
    }

    // ══ listarMisReportes ═════════════════════════════════════════════════════

    @Test
    @DisplayName("Lista los reportes del vendedor correctamente")
    void listarMisReportes_exitoso() {
        Page<Reporte> page = new PageImpl<>(List.of(reporte));
        when(repo.findByVendedorId(eq(1), any(Pageable.class))).thenReturn(page);
        when(reporteMapper.toResponseDTO(reporte)).thenReturn(responseDTO);

        PaginaResponseDTO<ReporteResponseDTO> resultado = reporteService.listarMisReportes(1, 0, 10);

        assertEquals(1, resultado.getContenido().size());
        assertEquals("No puedo subir fotos", resultado.getContenido().get(0).getAsunto());
        verify(repo, times(1)).findByVendedorId(eq(1), any(Pageable.class));
    }

    @Test
    @DisplayName("Devuelve lista vacía si el vendedor no tiene reportes")
    void listarMisReportes_listaVacia() {
        Page<Reporte> page = new PageImpl<>(List.of());
        when(repo.findByVendedorId(eq(1), any(Pageable.class))).thenReturn(page);

        PaginaResponseDTO<ReporteResponseDTO> resultado = reporteService.listarMisReportes(1, 0, 10);

        assertTrue(resultado.getContenido().isEmpty());
    }

    @Test
    @DisplayName("Lista múltiples reportes del vendedor en orden correcto")
    void listarMisReportes_multiplesReportes() {
        Reporte reporte2 = new Reporte();
        reporte2.setId(2);
        reporte2.setAsunto("Error en el login");
        reporte2.setEstado("ABIERTO");
        reporte2.setVendedor(vendedor);

        ReporteResponseDTO responseDTO2 = new ReporteResponseDTO();
        responseDTO2.setId(2);
        responseDTO2.setAsunto("Error en el login");

        Page<Reporte> page = new PageImpl<>(List.of(reporte, reporte2));
        when(repo.findByVendedorId(eq(1), any(Pageable.class))).thenReturn(page);
        when(reporteMapper.toResponseDTO(reporte)).thenReturn(responseDTO);
        when(reporteMapper.toResponseDTO(reporte2)).thenReturn(responseDTO2);

        PaginaResponseDTO<ReporteResponseDTO> resultado = reporteService.listarMisReportes(1, 0, 10);

        assertEquals(2, resultado.getContenido().size());
        assertEquals("No puedo subir fotos", resultado.getContenido().get(0).getAsunto());
        assertEquals("Error en el login", resultado.getContenido().get(1).getAsunto());
    }
}