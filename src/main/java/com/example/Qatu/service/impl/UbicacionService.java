package com.example.Qatu.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.Qatu.config.UmbralConfig;
import com.example.Qatu.dto.CongestionEventDTO;
import com.example.Qatu.dto.UbicacionEventDTO;
import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IUbicacionService;
import com.example.Qatu.service.IWebSoketSevice;
import com.example.Qatu.util.GeoUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UbicacionService extends GenericService<Ubicacion, Integer> implements IUbicacionService {
    private final UbicacionRepo repo;
    private final VendedorRepo vendedorRepo;
    private final IWebSoketSevice webSocketService;
    private final HeatmapService heatmapService;
    private final SugerenciaDeReasignacionService sugerenciaService;
    private final UmbralConfig umbralConfig;

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

        // 2. Contar vendedores ANTES de agregar el nuevo
        int vendedoresAntes = repo.contarEnRadio100m(dto.getLat(), dto.getLng());

        // 3. Desactivar ubicación anterior
        repo.desactivarPorVendedor(vendedorId);

        // 4. Crear nueva ubicación
        Ubicacion nueva = new Ubicacion();
        nueva.setVendedor(vendedor);
        nueva.setCoordenada(GeoUtils.crearPunto(dto.getLat(), dto.getLng()));
        nueva.setActivo(true);
        nueva.setTimestamp(LocalDateTime.now());
        Ubicacion guardada = repo.save(nueva);

        // 5. Emitir evento WebSocket
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

        // 6. Contar vendedores DESPUÉS de agregar el nuevo
        int vendedoresDespues = repo.contarEnRadio100m(dto.getLat(), dto.getLng());
        emitirNivelCongestion(dto.getLat(), dto.getLng(), vendedoresDespues);

        // 7. Evaluar sugerencia según tipo de desbordamiento
        if (vendedoresDespues >= umbralConfig.getUmbralRojo()
                && vendedoresAntes < umbralConfig.getUmbralRojo()) {
            // Este vendedor fue el que desbordó → ruta automática
            sugerenciaService.evaluarYEnviarRutaSugerida(
                    vendedorId, dto.getLat(), dto.getLng());
        } else if (vendedoresDespues >= umbralConfig.getUmbralRojo()) {
            // Ya estaba congestionado → sugerencia normal
            sugerenciaService.evaluarYEnviarSugerencia(
                    vendedorId, dto.getLat(), dto.getLng());
        }

        // 8. Armar response
        return UbicacionResponseDTO.builder()
                .ubicacionId(guardada.getId())
                .vendedorId(vendedorId)
                .lat(dto.getLat())
                .lng(dto.getLng())
                .timestamp(guardada.getTimestamp())
                .build();
    }

    // Determina el nivel de congestión y emite el evento
    // En UbicacionService.actualizarUbicacion(), reemplaza el método privado:
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
