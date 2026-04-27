package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Categoria;
import com.example.Qatu.repository.CategoriaRepo;
import com.example.Qatu.service.ICategoriaService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaService extends GenericService<Categoria, Integer> implements ICategoriaService{
    private final CategoriaRepo repo;

    @Override
    protected CategoriaRepo getRepo() {
        return repo;
    }
}
