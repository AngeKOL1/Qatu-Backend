package com.example.Qatu.repository;

import java.util.List;
import java.util.Optional;

import com.example.Qatu.models.Vendedor;
import com.example.Qatu.models.enums.EstadoVendedor;

public interface VendedorRepo extends GenericRepo<Vendedor, Integer>{
    boolean existsByEmail(String email);
    
    Optional<Vendedor> findByEmail(String email);

    List<Vendedor> findByEstado(EstadoVendedor estado );

} 
