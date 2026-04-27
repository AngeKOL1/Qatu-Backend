package com.example.Qatu.repository;

import java.util.Optional;

import com.example.Qatu.models.Categoria;

public interface CategoriaRepo extends GenericRepo<Categoria, Integer> {
    Optional<Categoria> findByNombre(String nombre);
}
