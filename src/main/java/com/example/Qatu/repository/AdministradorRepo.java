package com.example.Qatu.repository;

import java.util.Optional;

import com.example.Qatu.models.Administrador;

public interface AdministradorRepo extends GenericRepo<Administrador, Integer> {
    Optional<Administrador> findByEmail(String email);
}
    