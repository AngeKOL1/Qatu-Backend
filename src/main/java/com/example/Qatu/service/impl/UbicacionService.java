package com.example.Qatu.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Qatu.config.UmbralConfig;
import com.example.Qatu.dto.CongestionEventDTO;
import com.example.Qatu.dto.UbicacionEventDTO;
import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.dto.ZonaOcupacionProjectionDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.repository.ZonaRepo;
import com.example.Qatu.service.IUbicacionService;
import com.example.Qatu.service.IWebSoketSevice;
import com.example.Qatu.util.GeoUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UbicacionService extends GenericService<Ubicacion, Integer>
                implements IUbicacionService {

        private final UbicacionRepo repo;
        private final VendedorRepo vendedorRepo;
        private final IWebSoketSevice webSocketService;
        private final HeatmapService heatmapService;
        private final SugerenciaDeReasignacionService sugerenciaService;
        private final UmbralConfig umbralConfig;
        private final ZonaRepo zonaRepo;
        private final FcmService fcmService;

        @Override
        protected UbicacionRepo getRepo() {
                return repo;
        }

        @Override
        @Transactional
        public UbicacionResponseDTO actualizarUbicacion(
                        Integer vendedorId, UbicacionRequestDTO dto) {

                // 1. Verificar vendedor
                Vendedor vendedor = vendedorRepo.findById(vendedorId)
                                .orElseThrow(() -> new ModelNotFoundException("Vendedor no encontrado"));

                if (vendedor.getEstado() != EstadoVendedor.ACTIVO) {
                        throw new ModelNotFoundException("El vendedor no está activo");
                }

                // 2. Desactivar ubicación anterior
                repo.desactivarPorVendedor(vendedorId);

                // 3. Crear nueva ubicación
                Ubicacion nueva = new Ubicacion();
                nueva.setVendedor(vendedor);
                nueva.setCoordenada(GeoUtils.crearPunto(dto.getLat(), dto.getLng()));
                nueva.setActivo(true);
                nueva.setTimestamp(LocalDateTime.now());
                Ubicacion guardada = repo.save(nueva);

                // 4. Emitir evento WebSocket
                UbicacionEventDTO evento = UbicacionEventDTO.builder()
                                .evento("UBICACION_ACTUALIZADA")
                                .vendedorId(vendedorId)
                                .nombreNegocio(vendedor.getNombre())
                                .categoria(vendedor.getCategoria() != null
                                                ? vendedor.getCategoria().getNombre()
                                                : null)
                                .lat(dto.getLat())
                                .lng(dto.getLng())
                                .visible(vendedor.getVisible())
                                .timestamp(guardada.getTimestamp())
                                .build();

                webSocketService.emitirUbicacionActualizada(evento);

                // 5. Calcular congestión y emitir WebSocket
                int vendedoresDespues = repo.contarEnRadio100m(dto.getLat(), dto.getLng());
                emitirNivelCongestion(dto.getLat(), dto.getLng(), vendedoresDespues);

                // 6. Evaluar congestión por zona y enviar notificaciones
                evaluarCongestionPorZona(vendedor, dto.getLat(), dto.getLng(), vendedoresDespues);

                // 7. Armar response
                return UbicacionResponseDTO.builder()
                                .ubicacionId(guardada.getId())
                                .vendedorId(vendedorId)
                                .lat(dto.getLat())
                                .lng(dto.getLng())
                                .timestamp(guardada.getTimestamp())
                                .build();
        }

        // ── Lógica de congestión mejorada ─────────────────────────────────────────

        private void evaluarCongestionPorZona(
                        Vendedor vendedor, double lat, double lng, int vendedoresDespues) {

                Integer vendedorId = vendedor.getId();

                List<ZonaOcupacionProjectionDTO> zonas = zonaRepo.findZonasConOcupacionEnPunto(lat, lng);

                if (!zonas.isEmpty()) {
                        for (ZonaOcupacionProjectionDTO zona : zonas) {

                                // Zona RESTRINGIDA → notificar inmediatamente
                                if ("RESTRINGIDA".equals(zona.getTipoZona())) {
                                        log.info("Vendedor {} entró a zona RESTRINGIDA", vendedorId);

                                        List<Zona> zonasDisponibles = zonaRepo
                                                        .findZonasReasignacionDisponibles(lat, lng);

                                        if (!zonasDisponibles.isEmpty()
                                                        && vendedor.getFcmToken() != null) {
                                                Zona zonaAlternativa = zonasDisponibles.get(0);
                                                double[] destino = GeoUtils.calcularCentroide(
                                                                zonaAlternativa.getGeometria());

                                                fcmService.enviarNotificacionConRuta(
                                                                vendedor.getFcmToken(),
                                                                "Zona restringida",
                                                                "Estás en una zona donde no está permitido vender. " +
                                                                                "Te sugerimos moverte a: "
                                                                                + zonaAlternativa.getNombre(),
                                                                destino[0],
                                                                destino[1]);
                                        } else if (vendedor.getFcmToken() != null) {
                                                fcmService.enviarNotificacion(
                                                                vendedor.getFcmToken(),
                                                                "Zona restringida",
                                                                "Estás en una zona donde no está permitido vender. " +
                                                                                "Por favor reubícate.",
                                                                "ZONA_RESTRINGIDA");
                                        }
                                        return;
                                }

                                // Zona REASIGNACION → verificar capacidad
                                if ("REASIGNACION".equals(zona.getTipoZona())) {
                                        if (zona.getVendedoresActuales() >= zona.getCapacidadMaxima()) {
                                                log.info("Vendedor {} en zona REASIGNACION llena ({}/{})",
                                                                vendedorId,
                                                                zona.getVendedoresActuales(),
                                                                zona.getCapacidadMaxima());
                                                sugerenciaService.evaluarYEnviarRutaSugerida(
                                                                vendedorId, lat, lng);
                                        } else {
                                                log.info("Vendedor {} en zona REASIGNACION con espacio ({}/{})",
                                                                vendedorId,
                                                                zona.getVendedoresActuales(),
                                                                zona.getCapacidadMaxima());
                                        }
                                        return;
                                }
                        }
                }

                // Sin zona definida → umbral global como fallback
                if (vendedoresDespues >= umbralConfig.getUmbralRojo()) {
                        sugerenciaService.evaluarYEnviarSugerencia(vendedorId, lat, lng);
                }
        }

        // ── Emitir nivel de congestión por WebSocket ──────────────────────────────

        private void emitirNivelCongestion(double lat, double lng, int count) {
                String nivel = heatmapService.determinarNivel(count);

                CongestionEventDTO evento = CongestionEventDTO.builder()
                                .evento("ZONA_CONGESTIONADA")
                                .lat(lat)
                                .lng(lng)
                                .vendedoresCount(count)
                                .nivel(nivel)
                                .timestamp(LocalDateTime.now())
                                .build();

                webSocketService.emitirCongestion(evento);
        }
}