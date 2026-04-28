package com.example.Qatu.service;

import com.example.Qatu.dto.UbicacionRequestDTO;
import com.example.Qatu.dto.UbicacionResponseDTO;
import com.example.Qatu.models.Ubicacion;

public interface IUbicacionService extends IGenericService<Ubicacion, Integer> {
    UbicacionResponseDTO actualizarUbicacion(Integer vendedorId, UbicacionRequestDTO dto);
}
