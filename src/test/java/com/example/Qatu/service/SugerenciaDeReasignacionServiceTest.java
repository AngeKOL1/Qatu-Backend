package com.example.Qatu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.Qatu.dto.SugerenciaResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.SugerenciaMapper;
import com.example.Qatu.models.SugerenciaReasignacion;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.EstadoSugerencia;
import com.example.Qatu.models.enums.Movilidad;
import com.example.Qatu.models.enums.TipoZona;
import com.example.Qatu.repository.SugerenciaReasignacionRepo;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.repository.ZonaRepo;
import com.example.Qatu.service.impl.FcmService;
import com.example.Qatu.service.impl.SugerenciaDeReasignacionService;

@ExtendWith(MockitoExtension.class)
class SugerenciaDeReasignacionServiceTest {

    @Mock private SugerenciaReasignacionRepo repo;
    @Mock private VendedorRepo vendedorRepo;
    @Mock private ZonaRepo zonaRepo;
    @Mock private UbicacionRepo ubicacionRepo;
    @Mock private SugerenciaMapper mapper;
    @Mock private FcmService fcmService;

    @InjectMocks
    private SugerenciaDeReasignacionService sugerenciaService;

    private Vendedor vendedorCarrito;
    private Vendedor vendedorFijo;
    private Zona zona;
    private Ubicacion ubicacion;
    private SugerenciaReasignacion sugerencia;
    private SugerenciaResponseDTO responseDTO;

    private static final double LAT = -7.1638;
    private static final double LNG = -78.5001;

    @BeforeEach
    void setUp() {
        vendedorCarrito = new Vendedor();
        vendedorCarrito.setId(1);
        vendedorCarrito.setNombre("Carlos Quispe");
        vendedorCarrito.setTipoMovilidad(Movilidad.CARRITO);
        vendedorCarrito.setFcmToken("token-fcm-test");

        vendedorFijo = new Vendedor();
        vendedorFijo.setId(2);
        vendedorFijo.setNombre("Ana García");
        vendedorFijo.setTipoMovilidad(Movilidad.FIJO);

        zona = new Zona();
        zona.setId(1);
        zona.setNombre("Zona sur habilitada");
        zona.setTipoZona(TipoZona.REASIGNACION);
        zona.setCapacidadMaxima(15);
        zona.setActiva(true);

        ubicacion = new Ubicacion();
        ubicacion.setId(1);
        ubicacion.setVendedor(vendedorCarrito);
        ubicacion.setActivo(true);
        ubicacion.setTimestamp(LocalDateTime.now());

        sugerencia = new SugerenciaReasignacion();
        sugerencia.setId(1);
        sugerencia.setVendedor(vendedorCarrito);
        sugerencia.setZona(zona);
        sugerencia.setUbicacion(ubicacion);
        sugerencia.setEstado(EstadoSugerencia.ENVIADA);
        sugerencia.setFechaEnvio(LocalDateTime.now().minusHours(1));

        responseDTO = new SugerenciaResponseDTO();
        responseDTO.setId(1);
        responseDTO.setVendedorId(1);
        responseDTO.setEstado("ACEPTADA");
    }

    // ══ evaluarYEnviarSugerencia ══════════════════════════════════════════════

    @Test
    @DisplayName("Envía sugerencia correctamente a vendedor CARRITO en zona roja")
    void evaluar_exitoso_carrito() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedorCarrito));
        when(repo.findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(1, EstadoSugerencia.ENVIADA))
            .thenReturn(Optional.empty()); // sin cooldown
        when(zonaRepo.findZonasReasignacionDisponibles(LAT, LNG))
            .thenReturn(List.of(zona));
        when(ubicacionRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(Optional.of(ubicacion));
        when(repo.save(any())).thenReturn(sugerencia);

        sugerenciaService.evaluarYEnviarSugerencia(1, LAT, LNG);

        verify(repo, times(1)).save(any(SugerenciaReasignacion.class));
        verify(fcmService, times(1)).enviarNotificacion(
            eq("token-fcm-test"),
            eq("Zona congestionada"),
            contains("Zona sur habilitada"),
            eq("SUGERENCIA_REASIGNACION")
        );
    }

    @Test
    @DisplayName("Envía sugerencia correctamente a vendedor CAMIONETA en zona roja")
    void evaluar_exitoso_camioneta() {
        vendedorCarrito.setTipoMovilidad(Movilidad.CAMIONETA);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedorCarrito));
        when(repo.findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(1, EstadoSugerencia.ENVIADA))
            .thenReturn(Optional.empty());
        when(zonaRepo.findZonasReasignacionDisponibles(LAT, LNG))
            .thenReturn(List.of(zona));
        when(ubicacionRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(Optional.of(ubicacion));
        when(repo.save(any())).thenReturn(sugerencia);

        sugerenciaService.evaluarYEnviarSugerencia(1, LAT, LNG);

        verify(repo, times(1)).save(any());
    }

    @Test
    @DisplayName("No envía sugerencia a vendedor FIJO — RN-03")
    void evaluar_vendedorFijo_noEnviaSugerencia() {
        when(vendedorRepo.findById(2)).thenReturn(Optional.of(vendedorFijo));

        sugerenciaService.evaluarYEnviarSugerencia(2, LAT, LNG);

        verify(repo, never()).save(any());
        verify(fcmService, never()).enviarNotificacion(any(), any(), any(), any());
    }

    @Test
    @DisplayName("No envía sugerencia si el vendedor está en cooldown — RN-10")
    void evaluar_dentroDeCooldown_noEnvia() {
        // Última sugerencia enviada hace 10 minutos (dentro del cooldown de 30)
        SugerenciaReasignacion sugerenciaReciente = new SugerenciaReasignacion();
        sugerenciaReciente.setFechaEnvio(LocalDateTime.now().minusMinutes(10));

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedorCarrito));
        when(repo.findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(1, EstadoSugerencia.ENVIADA))
            .thenReturn(Optional.of(sugerenciaReciente));

        sugerenciaService.evaluarYEnviarSugerencia(1, LAT, LNG);

        verify(repo, never()).save(any());
        verify(fcmService, never()).enviarNotificacion(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Sí envía sugerencia si el cooldown ya expiró")
    void evaluar_cooldownExpirado_envia() {
        // Última sugerencia hace 45 minutos (fuera del cooldown de 30)
        SugerenciaReasignacion sugerenciaAntigua = new SugerenciaReasignacion();
        sugerenciaAntigua.setFechaEnvio(LocalDateTime.now().minusMinutes(45));

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedorCarrito));
        when(repo.findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(1, EstadoSugerencia.ENVIADA))
            .thenReturn(Optional.of(sugerenciaAntigua));
        when(zonaRepo.findZonasReasignacionDisponibles(LAT, LNG))
            .thenReturn(List.of(zona));
        when(ubicacionRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(Optional.of(ubicacion));
        when(repo.save(any())).thenReturn(sugerencia);

        sugerenciaService.evaluarYEnviarSugerencia(1, LAT, LNG);

        verify(repo, times(1)).save(any());
    }

    @Test
    @DisplayName("No envía sugerencia si no hay zonas disponibles")
    void evaluar_sinZonasDisponibles_noEnvia() {
        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedorCarrito));
        when(repo.findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(1, EstadoSugerencia.ENVIADA))
            .thenReturn(Optional.empty());
        when(zonaRepo.findZonasReasignacionDisponibles(LAT, LNG))
            .thenReturn(List.of());

        sugerenciaService.evaluarYEnviarSugerencia(1, LAT, LNG);

        verify(repo, never()).save(any());
        verify(fcmService, never()).enviarNotificacion(any(), any(), any(), any());
    }

    @Test
    @DisplayName("No envía FCM si el vendedor no tiene token registrado")
    void evaluar_sinFcmToken_noEnviaFcm() {
        vendedorCarrito.setFcmToken(null);

        when(vendedorRepo.findById(1)).thenReturn(Optional.of(vendedorCarrito));
        when(repo.findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(1, EstadoSugerencia.ENVIADA))
            .thenReturn(Optional.empty());
        when(zonaRepo.findZonasReasignacionDisponibles(LAT, LNG))
            .thenReturn(List.of(zona));
        when(ubicacionRepo.findByVendedorIdAndActivoTrue(1))
            .thenReturn(Optional.of(ubicacion));
        when(repo.save(any())).thenReturn(sugerencia);

        sugerenciaService.evaluarYEnviarSugerencia(1, LAT, LNG);

        // La sugerencia sí se guarda pero FCM no se llama
        verify(repo, times(1)).save(any());
        verify(fcmService, never()).enviarNotificacion(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Lanza excepción si el vendedor no existe")
    void evaluar_vendedorNoEncontrado() {
        when(vendedorRepo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> sugerenciaService.evaluarYEnviarSugerencia(99, LAT, LNG)
        );

        assertEquals("Vendedor no encontrado", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // ══ responderSugerencia ═══════════════════════════════════════════════════

    @Test
    @DisplayName("Vendedor acepta una sugerencia correctamente")
    void responder_aceptada() {
        when(repo.findById(1)).thenReturn(Optional.of(sugerencia));
        when(repo.save(any())).thenReturn(sugerencia);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        SugerenciaResponseDTO resultado =
            sugerenciaService.responderSugerencia(1, 1, EstadoSugerencia.ACEPTADA);

        assertNotNull(resultado);
        assertEquals(EstadoSugerencia.ACEPTADA, sugerencia.getEstado());
        assertNotNull(sugerencia.getFechaRespuesta());
        verify(repo, times(1)).save(sugerencia);
    }

    @Test
    @DisplayName("Vendedor ignora una sugerencia — RN-04 voluntario")
    void responder_ignorada() {
        when(repo.findById(1)).thenReturn(Optional.of(sugerencia));
        when(repo.save(any())).thenReturn(sugerencia);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        sugerenciaService.responderSugerencia(1, 1, EstadoSugerencia.IGNORADA);

        assertEquals(EstadoSugerencia.IGNORADA, sugerencia.getEstado());
        assertNotNull(sugerencia.getFechaRespuesta());
        verify(repo, times(1)).save(sugerencia);
    }

    @Test
    @DisplayName("Lanza excepción si la sugerencia no existe")
    void responder_sugerenciaNoEncontrada() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        ModelNotFoundException ex = assertThrows(
            ModelNotFoundException.class,
            () -> sugerenciaService.responderSugerencia(99, 1, EstadoSugerencia.ACEPTADA)
        );

        assertEquals("Sugerencia no encontrada", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si la sugerencia no pertenece al vendedor")
    void responder_noEsDueno() {
        when(repo.findById(1)).thenReturn(Optional.of(sugerencia));

        // Vendedor 2 intenta responder sugerencia del vendedor 1
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> sugerenciaService.responderSugerencia(1, 2, EstadoSugerencia.ACEPTADA)
        );

        assertEquals("No tienes permiso para responder esta sugerencia",
            ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Lanza excepción si la sugerencia ya fue respondida")
    void responder_yaRespondida() {
        sugerencia.setEstado(EstadoSugerencia.ACEPTADA); // ya respondida
        when(repo.findById(1)).thenReturn(Optional.of(sugerencia));

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> sugerenciaService.responderSugerencia(1, 1, EstadoSugerencia.IGNORADA)
        );

        assertEquals("Esta sugerencia ya fue respondida", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // ══ listarPorVendedor ═════════════════════════════════════════════════════

    @Test
    @DisplayName("Lista el historial de sugerencias del vendedor correctamente")
    void listar_exitoso() {
        when(repo.findByVendedorIdOrderByFechaEnvioDesc(1))
            .thenReturn(List.of(sugerencia));
        when(mapper.toResponseDTO(sugerencia)).thenReturn(responseDTO);

        List<SugerenciaResponseDTO> resultado =
            sugerenciaService.listarPorVendedor(1);

        assertEquals(1, resultado.size());
        verify(repo, times(1)).findByVendedorIdOrderByFechaEnvioDesc(1);
    }

    @Test
    @DisplayName("Devuelve lista vacía si el vendedor no tiene sugerencias")
    void listar_listaVacia() {
        when(repo.findByVendedorIdOrderByFechaEnvioDesc(1))
            .thenReturn(List.of());

        List<SugerenciaResponseDTO> resultado =
            sugerenciaService.listarPorVendedor(1);

        assertTrue(resultado.isEmpty());
    }
}