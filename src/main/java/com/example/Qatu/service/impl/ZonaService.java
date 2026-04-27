package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Zona;
import com.example.Qatu.repository.ZonaRepo;
import com.example.Qatu.service.IZonaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ZonaService extends GenericService<Zona, Integer> implements IZonaService {
    private final ZonaRepo repo;
    @Override
    protected ZonaRepo getRepo() {
        return repo;
    }
}
