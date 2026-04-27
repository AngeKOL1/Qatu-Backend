package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Ubicacion;
import com.example.Qatu.repository.UbicacionRepo;
import com.example.Qatu.service.IUbicacionService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UbicacionService extends GenericService<Ubicacion, Integer> implements IUbicacionService {
    private final UbicacionRepo repo;
    @Override
    protected UbicacionRepo getRepo() {
        return repo;
    } 
}
