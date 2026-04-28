package com.example.Qatu.repository;

import java.util.Optional;

import com.example.Qatu.models.UsuarioObservador;

public interface UsuarioObservadorRepo extends GenericRepo<UsuarioObservador, Integer> {
    Optional<UsuarioObservador> findByEmail(String email);
    boolean existsByEmail(String email);
} 