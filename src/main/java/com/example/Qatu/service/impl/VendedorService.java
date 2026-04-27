package com.example.Qatu.service.impl;

import org.springframework.stereotype.Service;

import com.example.Qatu.models.Vendedor;
import com.example.Qatu.repository.VendedorRepo;
import com.example.Qatu.service.IVendedorService;

import lombok.AllArgsConstructor;
@Service
@AllArgsConstructor
public class VendedorService extends GenericService<Vendedor, Integer> implements IVendedorService {
    private final VendedorRepo repo;
    @Override
    protected VendedorRepo getRepo() {
        return repo;
    }
}
