package com.example.Qatu.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Qatu.dto.SugerenciaResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.SugerenciaMapper;
import com.example.Qatu.models.SugerenciaReasignacion;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.EstadoSugerencia;
import com.example.Qatu.models.enums.Movilidad;
import com.example.Qatu.repository.SugerenciaReasignacionRepo;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.repository.ZonaRepo;
import com.example.Qatu.service.ISugerenciaReasignacionService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SugerenciaDeReasignacionService extends GenericService<SugerenciaReasignacion, Integer>
        implements ISugerenciaReasignacionService {

    private final SugerenciaReasignacionRepo repo;

    private final VendedorRepo vendedorRepo;
    private final ZonaRepo zonaRepo;
    private final UbicacionRepo ubicacionRepo;
    private final SugerenciaMapper mapper;
    private final FcmService fcmService;

    private static final int COOLDOWN_MINUTOS = 30;

    @Override
    protected SugerenciaReasignacionRepo getRepo() {
        return repo;
    }
    // ── Llamado desde UbicacionService cuando detecta zona ROJA ──────────────

    @Override
    public void evaluarYEnviarSugerencia(Integer vendedorId, double lat, double lng) {

        Vendedor vendedor = vendedorRepo.findById(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

        // RN-03: solo CARRITO o CAMIONETA reciben sugerencias
        if (vendedor.getTipoMovilidad() == Movilidad.FIJO) {
            log.info("Vendedor {} es FIJO — no recibe sugerencias", vendedorId);
            return;
        }

        // RN-10: cooldown de 30 minutos
        if (estaDentroDeCooldown(vendedorId)) {
            log.info("Vendedor {} en cooldown — sugerencia no enviada", vendedorId);
            return;
        }

        // Buscar zonas de reasignación disponibles cerca
        List<Zona> zonasDisponibles = zonaRepo
                .findZonasReasignacionDisponibles(lat, lng);

        if (zonasDisponibles.isEmpty()) {
            log.info("No hay zonas disponibles cerca del vendedor {}", vendedorId);
            return;
        }

        // Tomar la zona más cercana
        Zona zonaSugerida = zonasDisponibles.get(0);

        // Obtener ubicación actual del vendedor
        Ubicacion ubicacionActual = ubicacionRepo
                .findByVendedorIdAndActivoTrue(vendedorId)
                .orElseThrow(() -> new ModelNotFoundException("Ubicación no encontrada"));

        // Crear la sugerencia
        SugerenciaReasignacion sugerencia = new SugerenciaReasignacion();
        sugerencia.setVendedor(vendedor);
        sugerencia.setZona(zonaSugerida);
        sugerencia.setUbicacion(ubicacionActual);
        repo.save(sugerencia);

        // Enviar notificación FCM
        if (vendedor.getFcmToken() != null) {
            fcmService.enviarNotificacion(
                    vendedor.getFcmToken(),
                    "Zona congestionada",
                    "Hay una zona disponible cerca: " + zonaSugerida.getNombre()
                            + ". ¿Deseas moverte?",
                    "SUGERENCIA_REASIGNACION");
        }

        log.info("Sugerencia enviada al vendedor {} → zona {}",
                vendedorId, zonaSugerida.getNombre());
    }

    // ── Vendedor responde la sugerencia ───────────────────────────────────────

    @Override
    @Transactional
    public SugerenciaResponseDTO responderSugerencia(
            Integer sugerenciaId,
            Integer vendedorId,
            EstadoSugerencia accion) {

        SugerenciaReasignacion sugerencia = repo.findById(sugerenciaId)
                .orElseThrow(() -> new ModelNotFoundException("Sugerencia no encontrada"));

        // Verificar que la sugerencia pertenece al vendedor autenticado
        if (!sugerencia.getVendedor().getId().equals(vendedorId)) {
            throw new IllegalArgumentException(
                    "No tienes permiso para responder esta sugerencia");
        }

        // Solo se puede responder si está en estado ENVIADA
        if (sugerencia.getEstado() != EstadoSugerencia.ENVIADA) {
            throw new IllegalArgumentException(
                    "Esta sugerencia ya fue respondida");
        }

        // RN-04: la reasignación es siempre voluntaria
        sugerencia.setEstado(accion);
        sugerencia.setFechaRespuesta(LocalDateTime.now());

        return mapper.toResponseDTO(repo.save(sugerencia));
    }

    // ── Historial del vendedor ────────────────────────────────────────────────
    @Override
    public List<SugerenciaResponseDTO> listarPorVendedor(Integer vendedorId) {
        return repo
                .findByVendedorIdOrderByFechaEnvioDesc(vendedorId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    // ── Helper — verifica cooldown ────────────────────────────────────────────
    private boolean estaDentroDeCooldown(Integer vendedorId) {
        return repo
                .findTopByVendedorIdAndEstadoOrderByFechaEnvioDesc(
                        vendedorId, EstadoSugerencia.ENVIADA)
                .map(s -> s.getFechaEnvio()
                        .isAfter(LocalDateTime.now().minusMinutes(COOLDOWN_MINUTOS)))
                .orElse(false);
    }

}
