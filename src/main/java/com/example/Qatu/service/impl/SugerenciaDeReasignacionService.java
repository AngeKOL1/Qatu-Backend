package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.SugerenciaReasignacion;
import com.example.Qatu.repository.SugerenciaReasignacionRepo;
import com.example.Qatu.service.ISugerenciaReasignacionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SugerenciaDeReasignacionService extends GenericService<SugerenciaReasignacion, Integer> implements ISugerenciaReasignacionService {
    private final SugerenciaReasignacionRepo repo;
    @Override
    protected SugerenciaReasignacionRepo getRepo() {
        return repo;
    }
    
}
