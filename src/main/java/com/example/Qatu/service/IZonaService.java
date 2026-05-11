package com.example.Qatu.service;

import java.util.List;

import com.example.Qatu.dto.ZonaRequestDTO;
import com.example.Qatu.dto.ZonaResponseDTO;
import com.example.Qatu.models.Zona;
import com.example.Qatu.models.enums.TipoZona;

public interface IZonaService extends IGenericService<Zona, Integer> {
    List<ZonaResponseDTO> listarZonasActivas();
    List<ZonaResponseDTO> listarPorTipo(TipoZona tipo);
    ZonaResponseDTO crearZona(ZonaRequestDTO dto, Integer adminId);
    ZonaResponseDTO actualizarZona(Integer zonaId, ZonaRequestDTO dto);
    void desactivarZona(Integer zonaId);
}
