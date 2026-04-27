package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.UsuarioObservador;
import com.example.Qatu.repository.UsuarioObservadorRepo;
import com.example.Qatu.service.IUsuarioObservadorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioObservadorService extends GenericService<UsuarioObservador, Integer> implements IUsuarioObservadorService {
    private final UsuarioObservadorRepo repo;
    @Override
    protected UsuarioObservadorRepo getRepo() {
        return repo;
    }
}
