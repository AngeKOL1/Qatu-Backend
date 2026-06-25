package com.example.Qatu.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.Qatu.dto.PaginaResponseDTO;
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
import com.example.Qatu.util.GeoUtils;
import com.example.Qatu.util.PaginacionUtils;

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

                // RN-03: solo CARRITO o CAMIONETA
                if (vendedor.getTipoMovilidad() == Movilidad.FIJO) {
                        log.info("Vendedor {} es FIJO — no recibe sugerencias", vendedorId);
                        return;
                }

                // RN-10: cooldown de 30 minutos
                if (estaDentroDeCooldown(vendedorId)) {
                        log.info("Vendedor {} en cooldown — sugerencia no enviada", vendedorId);
                        return;
                }

                // Buscar zonas disponibles cercanas
                List<Zona> zonasDisponibles = zonaRepo
                                .findZonasReasignacionDisponibles(lat, lng);

                if (zonasDisponibles.isEmpty()) {
                        log.info("No hay zonas disponibles cerca del vendedor {}", vendedorId);
                        return;
                }

                Zona zonaSugerida = zonasDisponibles.get(0);

                // ← corregido: usa findFirst para evitar NonUniqueResultException
                Ubicacion ubicacionActual = ubicacionRepo
                                .findFirstByVendedorIdAndActivoTrueOrderByTimestampDesc(vendedorId)
                                .orElseThrow(() -> new ModelNotFoundException("Ubicación no encontrada"));

                SugerenciaReasignacion sugerencia = new SugerenciaReasignacion();
                sugerencia.setVendedor(vendedor);
                sugerencia.setZona(zonaSugerida);
                sugerencia.setUbicacion(ubicacionActual);
                repo.save(sugerencia);

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

        @Override
        public void evaluarYEnviarRutaSugerida(
                        Integer vendedorId, double lat, double lng) {

                Vendedor vendedor = vendedorRepo.findById(vendedorId)
                                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

                // RN-03: solo CARRITO o CAMIONETA
                if (vendedor.getTipoMovilidad() == Movilidad.FIJO) {
                        log.info("Vendedor {} es FIJO — sin ruta sugerida", vendedorId);
                        return;
                }

                // RN-10: cooldown 30 minutos
                if (estaDentroDeCooldown(vendedorId)) {
                        log.info("Vendedor {} en cooldown — ruta no enviada", vendedorId);
                        return;
                }

                // Buscar zona disponible más cercana
                List<Zona> zonasDisponibles = zonaRepo
                                .findZonasReasignacionDisponibles(lat, lng);

                if (zonasDisponibles.isEmpty()) {
                        log.info("Sin zonas disponibles para ruta del vendedor {}", vendedorId);
                        return;
                }

                Zona zonaSugerida = zonasDisponibles.get(0);

                // Calcular centroide de la zona como punto de destino
                double[] destino = GeoUtils.calcularCentroide(zonaSugerida.getGeometria());

                // Guardar sugerencia en BD
                Ubicacion ubicacionActual = ubicacionRepo
                                .findFirstByVendedorIdAndActivoTrueOrderByTimestampDesc(vendedorId)
                                .orElseThrow(() -> new ModelNotFoundException("Ubicación no encontrada"));

                SugerenciaReasignacion sugerencia = new SugerenciaReasignacion();
                sugerencia.setVendedor(vendedor);
                sugerencia.setZona(zonaSugerida);
                sugerencia.setUbicacion(ubicacionActual);
                repo.save(sugerencia);

                // Enviar FCM con coordenadas de destino para trazar ruta en Flutter
                if (vendedor.getFcmToken() != null) {
                        fcmService.enviarNotificacionConRuta(
                                        vendedor.getFcmToken(),
                                        "Zona congestionada — ruta sugerida",
                                        "Te sugerimos moverte a: " + zonaSugerida.getNombre(),
                                        destino[0],
                                        destino[1]);
                }

                log.info("Ruta sugerida enviada al vendedor {} → zona {} ({}, {})",
                                vendedorId, zonaSugerida.getNombre(), destino[0], destino[1]);
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
        public PaginaResponseDTO<SugerenciaResponseDTO> listarPorVendedor(
                        Integer vendedorId, int pagina, int tamanio) {

                Pageable pageable = PageRequest.of(pagina, tamanio,
                                Sort.by("fechaEnvio").descending());

                Page<SugerenciaReasignacion> page = repo
                                .findByVendedorIdOrderByFechaEnvioDesc(vendedorId, pageable);

                return PaginacionUtils.construir(
                                page.map(mapper::toResponseDTO));
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
