package com.example.Qatu.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.Qatu.dto.CongestionEventDTO;
import com.example.Qatu.dto.UbicacionEventDTO;
import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
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
    private final UbicacionRepo ubicacionRepo;
    private final IWebSoketSevice webSocketService;

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
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        if (vendedor.getEstado() != EstadoVendedor.ACTIVO) {
            throw new RuntimeException("El vendedor no está activo");
        }

        // 2. Desactivar ubicación anterior
        ubicacionRepo.desactivarPorVendedor(vendedorId);

        // 3. Crear nueva ubicación
        Ubicacion nueva = new Ubicacion();
        nueva.setVendedor(vendedor);
        nueva.setCoordenada(GeoUtils.crearPunto(dto.getLat(), dto.getLng()));
        nueva.setActivo(true);
        nueva.setTimestamp(LocalDateTime.now());
        Ubicacion guardada = ubicacionRepo.save(nueva);

        // 4. Emitir evento WebSocket ← NUEVO
        UbicacionEventDTO evento = UbicacionEventDTO.builder()
            .evento("UBICACION_ACTUALIZADA")
            .vendedorId(vendedorId)
            .nombreNegocio(vendedor.getNombre())
            .categoria(vendedor.getCategoria() != null
                ? vendedor.getCategoria().getNombre() : null)
            .lat(dto.getLat())
            .lng(dto.getLng())
            .visible(vendedor.getVisible())
            .timestamp(guardada.getTimestamp())
            .build();

        webSocketService.emitirUbicacionActualizada(evento);

        // 5. Calcular congestión y emitir si supera umbral ← NUEVO
        int vendedoresEnZona = ubicacionRepo.contarEnRadio100m(
            dto.getLat(), dto.getLng());
        emitirNivelCongestion(dto.getLat(), dto.getLng(), vendedoresEnZona);

        // 6. Armar response
        return UbicacionResponseDTO.builder()
            .ubicacionId(guardada.getId())
            .vendedorId(vendedorId)
            .lat(dto.getLat())
            .lng(dto.getLng())
            .timestamp(guardada.getTimestamp())
            .build();
    }

    // Determina el nivel de congestión y emite el evento
    private void emitirNivelCongestion(double lat, double lng, int count) {
        String nivel;
        if (count >= 10)      nivel = "ROJO";
        else if (count >= 5)  nivel = "AMARILLO";
        else                  nivel = "VERDE";

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
