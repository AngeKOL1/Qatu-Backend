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
import org.locationtech.jts.geom.Polygon;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Qatu.dto.ZonaRequestDTO;
import com.example.Qatu.dto.ZonaResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ZonaMapper;
import com.example.Qatu.models.Administrador;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.TipoZona;
import com.example.Qatu.repository.AdministradorRepo;
import com.example.Qatu.repository.ZonaRepo;
import com.example.Qatu.service.impl.WebSocketService;
import com.example.Qatu.service.impl.ZonaService;
import com.example.Qatu.util.GeoUtils;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ZonaServiceTest {

    @Mock private ZonaRepo repo;
    @Mock private ZonaMapper mapper;
    @Mock private AdministradorRepo administradorRepo;
    @Mock private WebSocketService webSocketService;

    @InjectMocks
    private ZonaService zonaService;

    private Zona zona;
    private ZonaRequestDTO requestDTO;
    private ZonaResponseDTO responseDTO;
    private Administrador admin;
    private Polygon poligono;

    @BeforeEach
    void setUp() {
        admin = new Administrador();
        admin.setId(1);
        admin.setNombre("Administrador Qatu");
        admin.setEmail("admin@qatu.com");

        // Crear polígono real con JTS para pruebas
        poligono = GeoUtils.crearPoligono(List.of(
            List.of(-78.501, -7.163),
            List.of(-78.500, -7.163),
            List.of(-78.500, -7.164),
            List.of(-78.501, -7.164),
            List.of(-78.501, -7.163)
        ));

        zona = new Zona();
        zona.setId(1);
        zona.setNombre("Zona restringida entrada");
        zona.setDescripcion("Prohibido vender aquí");
        zona.setTipoZona(TipoZona.RESTRINGIDA);
        zona.setCapacidadMaxima(0);
        zona.setActiva(true);
        zona.setGeometria(poligono);
        zona.setAdministrador(admin);
        zona.setFechaCreacion(LocalDateTime.now());

        requestDTO = new ZonaRequestDTO();
        requestDTO.setNombre("Zona restringida entrada");
        requestDTO.setDescripcion("Prohibido vender aquí");
        requestDTO.setTipoZona(TipoZona.RESTRINGIDA);
        requestDTO.setCapacidadMaxima(0);
        requestDTO.setFechaExpiracion(null);
        requestDTO.setCoordenadas(List.of(
            List.of(-78.501, -7.163),
            List.of(-78.500, -7.163),
            List.of(-78.500, -7.164),
            List.of(-78.501, -7.164),
            List.of(-78.501, -7.163)
        ));

        responseDTO = new ZonaResponseDTO();
        responseDTO.setId(1);
        responseDTO.setNombre("Zona restringida entrada");
        responseDTO.setTipoZona("RESTRINGIDA");
        responseDTO.setActiva(true);
        responseDTO.setAdministradorId(1);
        responseDTO.setCoordenadas(requestDTO.getCoordenadas());
    }

    // ══ listarZonasActivas ════════════════════════════════════════════════════

    @Test
    @DisplayName("Lista todas las zonas activas correctamente")
    void listarZonasActivas_exitoso() {
        when(repo.findByActivaTrue()).thenReturn(List.of(zona));
        when(mapper.toResponseDTO(zona)).thenReturn(responseDTO);

        List<ZonaResponseDTO> resultado = zonaService.listarZonasActivas();

        assertEquals(1, resultado.size());
        assertEquals("Zona restringida entrada", resultado.get(0).getNombre());
        verify(repo, times(1)).findByActivaTrue();
    }

    @Test
    @DisplayName("Devuelve lista vacía si no hay zonas activas")
    void listarZonasActivas_listaVacia() {
        when(repo.findByActivaTrue()).thenReturn(List.of());

        List<ZonaResponseDTO> resultado = zonaService.listarZonasActivas();

        assertTrue(resultado.isEmpty());
    }

    // ══ listarPorTipo ═════════════════════════════════════════════════════════

    @Test
    @DisplayName("Lista zonas activas filtrando por tipo RESTRINGIDA")
    void listarPorTipo_restringida() {
        when(repo.findByTipoZonaAndActivaTrue(TipoZona.RESTRINGIDA))
            .thenReturn(List.of(zona));
        when(mapper.toResponseDTO(zona)).thenReturn(responseDTO);

        List<ZonaResponseDTO> resultado =
            zonaService.listarPorTipo(TipoZona.RESTRINGIDA);

        assertEquals(1, resultado.size());
        assertEquals("RESTRINGIDA", resultado.get(0).getTipoZona());
        verify(repo, times(1)).findByTipoZonaAndActivaTrue(TipoZona.RESTRINGIDA);
    }

    @Test
    @DisplayName("Lista zonas activas filtrando por tipo REASIGNACION")
    void listarPorTipo_reasignacion() {
        Zona zonaReasig = new Zona();
        zonaReasig.setId(2);
        zonaReasig.setNombre("Zona sur habilitada");
        zonaReasig.setTipoZona(TipoZona.REASIGNACION);
        zonaReasig.setCapacidadMaxima(15);
        zonaReasig.setActiva(true);
        zonaReasig.setGeometria(poligono);
        zonaReasig.setAdministrador(admin);

        ZonaResponseDTO responseDTOReasig = new ZonaResponseDTO();
        responseDTOReasig.setId(2);
        responseDTOReasig.setTipoZona("REASIGNACION");

        when(repo.findByTipoZonaAndActivaTrue(TipoZona.REASIGNACION))
            .thenReturn(List.of(zonaReasig));
        when(mapper.toResponseDTO(zonaReasig)).thenReturn(responseDTOReasig);

        List<ZonaResponseDTO> resultado =
            zonaService.listarPorTipo(TipoZona.REASIGNACION);

        assertEquals(1, resultado.size());
        assertEquals("REASIGNACION", resultado.get(0).getTipoZona());
    }

    @Test
    @DisplayName("Devuelve lista vacía si no hay zonas del tipo solicitado")
    void listarPorTipo_listaVacia() {
        when(repo.findByTipoZonaAndActivaTrue(TipoZona.REASIGNACION))
            .thenReturn(List.of());

        List<ZonaResponseDTO> resultado =
            zonaService.listarPorTipo(TipoZona.REASIGNACION);

        assertTrue(resultado.isEmpty());
    }

    // ══ crearZona ═════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Crea una zona correctamente y emite evento WebSocket")
    void crearZona_exitoso() {
        when(administradorRepo.findById(1)).thenReturn(Optional.of(admin));
        when(mapper.toEntity(requestDTO)).thenReturn(zona);
        when(repo.save(any())).thenReturn(zona);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        ZonaResponseDTO resultado = zonaService.crearZona(requestDTO, 1);

        assertNotNull(resultado);
        assertEquals("Zona restringida entrada", resultado.getNombre());

        // Verifica que el admin fue asignado
        verify(administradorRepo, times(1)).findById(1);
        verify(repo, times(1)).save(any());

        // Verifica que se emitió el evento WebSocket
        verify(webSocketService, times(1)).emitirZonaCreada(any());
    }

    @Test
    @DisplayName("Lanza excepción si el admin no existe al crear zona")
    void crearZona_adminNoEncontrado() {
        when(administradorRepo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> zonaService.crearZona(requestDTO, 99)
        );

        assertEquals("Administrador no encontrado", ex.getMessage());
        verify(repo, never()).save(any());
        verify(webSocketService, never()).emitirZonaCreada(any());
    }

    @Test
    @DisplayName("Asigna el administrador correctamente al crear la zona")
    void crearZona_asignaAdmin() {
        when(administradorRepo.findById(1)).thenReturn(Optional.of(admin));
        when(mapper.toEntity(requestDTO)).thenReturn(zona);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        zonaService.crearZona(requestDTO, 1);

        verify(repo).save(argThat(z ->
            z.getAdministrador() != null &&
            z.getAdministrador().getId().equals(1)
        ));
    }

    @Test
    @DisplayName("Siempre emite WebSocket al crear zona")
    void crearZona_emiteWebSocket() {
        when(administradorRepo.findById(1)).thenReturn(Optional.of(admin));
        when(mapper.toEntity(requestDTO)).thenReturn(zona);
        when(repo.save(any())).thenReturn(zona);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        zonaService.crearZona(requestDTO, 1);

        verify(webSocketService, times(1)).emitirZonaCreada(any(ZonaResponseDTO.class));
    }

    // ══ actualizarZona ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Actualiza una zona correctamente")
    void actualizarZona_exitoso() {
        ZonaRequestDTO updateDTO = new ZonaRequestDTO();
        updateDTO.setNombre("Zona restringida actualizada");
        updateDTO.setDescripcion("Nueva descripción");
        updateDTO.setTipoZona(TipoZona.RESTRINGIDA);
        updateDTO.setCapacidadMaxima(0);
        updateDTO.setCoordenadas(requestDTO.getCoordenadas());

        when(repo.findById(1)).thenReturn(Optional.of(zona));
        when(repo.save(any())).thenReturn(zona);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        ZonaResponseDTO resultado = zonaService.actualizarZona(1, updateDTO);

        assertNotNull(resultado);
        assertEquals("Zona restringida actualizada", zona.getNombre());
        assertEquals("Nueva descripción", zona.getDescripcion());
        verify(repo, times(1)).save(zona);
    }

    @Test
    @DisplayName("Lanza excepción si la zona no existe al actualizar")
    void actualizarZona_noEncontrada() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> zonaService.actualizarZona(99, requestDTO)
        );

        assertEquals("Zona no encontrada", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Actualiza el polígono de la zona correctamente")
    void actualizarZona_actualizaGeometria() {
        when(repo.findById(1)).thenReturn(Optional.of(zona));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        zonaService.actualizarZona(1, requestDTO);

        // Verifica que la geometría fue actualizada
        verify(repo).save(argThat(z ->
            z.getGeometria() != null
        ));
    }

    // ══ desactivarZona ════════════════════════════════════════════════════════

    @Test
    @DisplayName("Desactiva una zona correctamente")
    void desactivarZona_exitoso() {
        when(repo.findById(1)).thenReturn(Optional.of(zona));
        when(repo.save(any())).thenReturn(zona);

        zonaService.desactivarZona(1);

        assertFalse(zona.getActiva());
        verify(repo, times(1)).save(zona);
    }

    @Test
    @DisplayName("Lanza excepción si la zona no existe al desactivar")
    void desactivarZona_noEncontrada() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> zonaService.desactivarZona(99)
        );

        assertEquals("Zona no encontrada", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("La zona desactivada no aparece en el listado de activas")
    void desactivarZona_noApareceEnListado() {
        when(repo.findById(1)).thenReturn(Optional.of(zona));
        when(repo.save(any())).thenReturn(zona);

        zonaService.desactivarZona(1);

        // Después de desactivar, findByActivaTrue no la devuelve
        when(repo.findByActivaTrue()).thenReturn(List.of());

        List<ZonaResponseDTO> resultado = zonaService.listarZonasActivas();

        assertTrue(resultado.isEmpty());
        assertFalse(zona.getActiva());
    }
}