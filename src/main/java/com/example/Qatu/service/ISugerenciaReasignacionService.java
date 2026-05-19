package com.example.Qatu.service;

import java.util.List;

import com.example.Qatu.dto.SugerenciaResponseDTO;
import com.example.Qatu.models.SugerenciaReasignacion;
import com.example.Qatu.models.enums.EstadoSugerencia;

public interface ISugerenciaReasignacionService extends IGenericService<SugerenciaReasignacion, Integer>{
    void evaluarYEnviarSugerencia(Integer vendedorId, double lat, double lng);
    SugerenciaResponseDTO responderSugerencia(Integer sugerenciaId, Integer vendedorId, EstadoSugerencia accion);
    List<SugerenciaResponseDTO> listarPorVendedor(Integer vendedorId);
}
