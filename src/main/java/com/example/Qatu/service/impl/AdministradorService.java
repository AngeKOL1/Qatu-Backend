package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Administrador;
import com.example.Qatu.repository.AdministradorRepo;
import com.example.Qatu.service.IAdministradorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AdministradorService extends GenericService<Administrador, Integer> implements IAdministradorService{
    private final AdministradorRepo repo;

    @Override
    protected AdministradorRepo getRepo() {
        return repo;
    }
    
}
