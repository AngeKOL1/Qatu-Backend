package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Producto;
import com.example.Qatu.repository.ProductoRepo;
import com.example.Qatu.service.IProductoService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoService extends GenericService<Producto, Integer> implements IProductoService {
    private final ProductoRepo repo;
    @Override
    protected ProductoRepo getRepo() {
        return repo;
    }
}
