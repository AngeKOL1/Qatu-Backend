package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Reporte;
import com.example.Qatu.repository.ReporteRepo;
import com.example.Qatu.service.IReporteService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReporteService extends GenericService<Reporte, Integer> implements IReporteService {
    private final ReporteRepo repo;
    @Override
    protected ReporteRepo getRepo() {
        return repo;
    }
    
}
