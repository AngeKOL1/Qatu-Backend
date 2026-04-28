package com.example.Qatu.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IUbicacionService;
import com.example.Qatu.util.GeoUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UbicacionService extends GenericService<Ubicacion, Integer> implements IUbicacionService {
    private final UbicacionRepo repo;
    private final VendedorRepo vendedorRepo;

    @Override
    protected UbicacionRepo getRepo() {
        return repo;
    } 
    @Override
    @Transactional
    public UbicacionResponseDTO actualizarUbicacion(Integer vendedorId, UbicacionRequestDTO dto) {
        // 1. Verificar que el vendedor existe y está activo
        Vendedor vendedor = vendedorRepo.findById(vendedorId)
            .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado"));

        if (vendedor.getEstado() != EstadoVendedor.ACTIVO) {
            throw new IllegalArgumentException("El vendedor no está activo");
        }

        // 2. Desactivar ubicación anterior
        repo.desactivarPorVendedor(vendedorId);

        // 3. Crear nueva ubicación
        Ubicacion nueva = new Ubicacion();
        nueva.setVendedor(vendedor);
        nueva.setCoordenada(GeoUtils.crearPunto(dto.getLat(), dto.getLng()));
        nueva.setActiva(true);
        nueva.setTimestamp(LocalDateTime.now());

        Ubicacion guardada = repo.save(nueva);

        return UbicacionResponseDTO.builder()
            .ubicacionId(guardada.getId())
            .vendedorId(vendedorId)
            .lat(dto.getLat())
            .lng(dto.getLng())
            .timestamp(guardada.getTimestamp())
            .build();
    }
}
