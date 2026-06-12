package com.example.Qatu.service;

import com.example.Qatu.dto.PaginaResponseDTO;
import com.example.Qatu.dto.SugerenciaResponseDTO;
import com.example.Qatu.models.SugerenciaReasignacion;
import com.example.Qatu.models.enums.EstadoSugerencia;

public interface ISugerenciaReasignacionService extends IGenericService<SugerenciaReasignacion, Integer>{
    void evaluarYEnviarSugerencia(Integer vendedorId, double lat, double lng);
    SugerenciaResponseDTO responderSugerencia(Integer sugerenciaId, Integer vendedorId, EstadoSugerencia accion);
    PaginaResponseDTO<SugerenciaResponseDTO> listarPorVendedor( Integer vendedorId, int pagina, int tamanio);
}
