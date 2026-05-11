package com.example.Qatu.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Qatu.dto.ZonaRequestDTO;
import com.example.Qatu.dto.ZonaResponseDTO;
import com.example.Qatu.exception.ModelNotFoundException;
import com.example.Qatu.mapper.ZonaMapper;
import com.example.Qatu.models.Administrador;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.TipoZona;
import com.example.Qatu.repository.AdministradorRepo;
import com.example.Qatu.repository.ZonaRepo;
import com.example.Qatu.service.IZonaService;
import com.example.Qatu.util.GeoUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ZonaService extends GenericService<Zona, Integer> implements IZonaService {
    private final ZonaRepo repo;
    private final ZonaMapper mapper; 
    private final AdministradorRepo administradorRepo;
    private final WebSocketService webSocketService;

    @Override
    protected ZonaRepo getRepo() {
        return repo;
    }

     // ── Públicos ──────────────────────────────────────────────────────────────

    @Override
    public List<ZonaResponseDTO> listarZonasActivas() {
        return repo.findByActivaTrue()
            .stream()
            .map(this::toResponseConCoordenadas)
            .toList();
    }

    @Override
    public List<ZonaResponseDTO> listarPorTipo(TipoZona tipo) {
        return repo.findByTipoZonaAndActivaTrue(tipo)
            .stream()
            .map(this::toResponseConCoordenadas)
            .toList();
    }

    // ── Admin ─────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ZonaResponseDTO crearZona(ZonaRequestDTO dto, Integer adminId) {

        Administrador admin = administradorRepo.findById(adminId)
            .orElseThrow(() -> new ModelNotFoundException("Administrador no encontrado"));

        Zona zona = mapper.toEntity(dto);
        zona.setAdministrador(admin);
        zona.setGeometria(GeoUtils.crearPoligono(dto.getCoordenadas()));
        zona.setFechaExpiracion(dto.getFechaExpiracion());

        Zona guardada = repo.save(zona);

        // Emitir evento WebSocket — el mapa se actualiza en tiempo real
        webSocketService.emitirZonaCreada(toResponseConCoordenadas(guardada));

        return toResponseConCoordenadas(guardada);
    }

    @Override
    @Transactional
    public ZonaResponseDTO actualizarZona(Integer zonaId, ZonaRequestDTO dto) {

        Zona zona = repo.findById(zonaId)
            .orElseThrow(() -> new ModelNotFoundException("Zona no encontrada"));

        zona.setNombre(dto.getNombre());
        zona.setDescripcion(dto.getDescripcion());
        zona.setTipoZona(dto.getTipoZona());
        zona.setCapacidadMaxima(dto.getCapacidadMaxima());
        zona.setFechaExpiracion(dto.getFechaExpiracion());
        zona.setGeometria(GeoUtils.crearPoligono(dto.getCoordenadas()));

        return toResponseConCoordenadas(repo.save(zona));
    }

    @Override
    @Transactional
    public void desactivarZona(Integer zonaId) {
        Zona zona = repo.findById(zonaId)
            .orElseThrow(() -> new ModelNotFoundException("Zona no encontrada"));

        zona.setActiva(false);
        repo.save(zona);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private ZonaResponseDTO toResponseConCoordenadas(Zona zona) {
        ZonaResponseDTO dto = mapper.toResponseDTO(zona);
        if (zona.getGeometria() != null) {
            dto.setCoordenadas(GeoUtils.extraerCoordenadas(zona.getGeometria()));
        }
        return dto;
    }
}
