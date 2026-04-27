package com.example.Qatu.repository;

import java.util.Optional;

import com.example.Qatu.models.Vendedor;

public interface VendedorRepo extends GenericRepo<Vendedor, Integer>{
    boolean existsByEmail(String email);
    
    Optional<Vendedor> findByEmail(String email);
} 
