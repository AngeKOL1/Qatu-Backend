package com.example.Qatu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.service.IVendedorService;

import lombok.AllArgsConstructor;

// VendedorController.java
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class VendedorController {

    private final IVendedorService vendedorService;
    private final VendedorMapper mapper;

    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar() {
        List<VendedorResponseDTO> vendedores = vendedorService.findAll()
            .stream()
            .map(mapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(vendedores);
    }

    // GET /api/vendedores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VendedorResponseDTO> obtener(@PathVariable Integer id) {
        Vendedor vendedor = vendedorService.findById(id);
        return ResponseEntity.ok(mapper.toResponseDTO(vendedor));
    }
}