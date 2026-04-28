package com.example.Qatu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Qatu.dto.EstadoActividadDTO;
import com.example.Qatu.dto.VendedorResponseDTO;
import com.example.Qatu.mapper.VendedorMapper;
import com.example.Qatu.models.Vendedor;
import com.example.Qatu.security.JwtTokenUtil;
import com.example.Qatu.service.IVendedorService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

// VendedorController.java
@RestController
@RequestMapping("/api/vendedores")
@AllArgsConstructor
public class VendedorController {

    private final IVendedorService vendedorService;
    private final VendedorMapper mapper;
    private final JwtTokenUtil jwtTokenUtil;

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

    @PatchMapping("/mi-estado")
    public ResponseEntity<VendedorResponseDTO> cambiarEstadoActividad(
            @Valid @RequestBody EstadoActividadDTO dto,
            @RequestHeader("Authorization") String token) {

        Integer vendedorId = jwtTokenUtil.getIdUsuarioFromToken(token);
        VendedorResponseDTO response = 
            vendedorService.cambiarVisibilidad(vendedorId, dto.getVisible());

        return ResponseEntity.ok(response);
    }
}